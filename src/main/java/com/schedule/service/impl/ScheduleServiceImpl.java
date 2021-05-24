package com.schedule.service.impl;

import com.schedule.Constants;
import com.schedule.model.Schedule;
import com.schedule.model.ScheduleServiceModel;
import com.schedule.repository.ScheduleRepository;
import com.schedule.rest.dto.ScheduleDto;
import com.schedule.service.ScheduleService;
import com.schedule.util.Aes256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    final ScheduleRepository scheduleRepository;
    private final Aes256 aes256 = new Aes256();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void encryptAllSchedulesWithNewKey() {
        aes256.encryptAllRecordWithNewKey((List<Schedule>) scheduleRepository.findAll()).forEach(scheduleRepository::save);
    }

    @Override
    public List<Schedule> getAll() {
        return getEncodedScheduleList(aes256.decryptScheduleListCopy((List<Schedule>) scheduleRepository.findAll()));
    }

    @Override
    public List<Schedule> getByUser(Integer userId) {
        List<Schedule> scheduleList = scheduleRepository.findByUserId(userId);
        return getEncodedScheduleList(listScheduleProcessing(aes256.decryptScheduleListCopy(scheduleList)));
    }

    @Override
    public List<Schedule> getById(Integer id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        return getEncodedScheduleList(changeModeProcessing(aes256.decryptScheduleCopy(schedule)));
    }

    @Override
    public List<Schedule> create(String startTime, String endTime, String date, Long duration, Integer userId, Integer mode) {
        Schedule schedule = new Schedule(startTime, endTime, date, duration, userId, new Timestamp(System.currentTimeMillis()), null, mode);
        scheduleRepository.save(aes256.encryptScheduleCopy(schedule));
        return changeModeProcessing(schedule);
    }

    @Override
    public List<Schedule> update(Integer id, String startTime, String endTime, String date, Long duration, Integer mode) {
        Schedule schedule = aes256.decryptSchedule(scheduleRepository.findById(id).get());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setDate(date);
        schedule.setDuration(duration);
        schedule.setModified(new Timestamp(System.currentTimeMillis()));
        schedule.setMode(mode);
        Schedule scheduleCopy = schedule.clone();
        scheduleRepository.save(aes256.encryptSchedule(schedule));
        return changeModeProcessing(scheduleCopy);
    }

    @Override
    public List<Schedule> getCurrentByUser(Integer userId) {
        java.util.Date dateNow = new java.util.Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        List<Schedule> schedules = scheduleRepository.findByDateAndUserId(aes256.encrypt(formatForDateNow.format(dateNow)), userId);
        if (schedules == null || schedules.size() == 0) {
            dateNow.setTime(dateNow.getTime() - Constants.COUNT_MS_ON_DAY);
            schedules = scheduleRepository.findByDateAndUserId(aes256.encrypt(formatForDateNow.format(dateNow)), userId);
        }

        return getEncodedScheduleList(listScheduleProcessing(aes256.decryptScheduleListCopy(schedules)));

    }

    @Override
    public void delete(Integer id) {
        scheduleRepository.deleteById(id);
    }


    private List<Schedule> changeModeProcessing(Schedule schedule) {
        if (schedule.getMode() == Constants.UNIFORM_DISTRIBUTION)
            return getEncodedScheduleList(uniformMethodScheduleProcessing(schedule));
        if (schedule.getMode() == Constants.BOUNDARY_DISTRIBUTION)
            return getEncodedScheduleList(boundaryMethodScheduleProcessing(schedule));
        return null;

    }


    private List<Schedule> listScheduleProcessing(List<Schedule> scheduleList) {
        List<Schedule> processedScheduleList = new ArrayList<>();
        for (Schedule schedule : scheduleList)
            processedScheduleList.addAll(Objects.requireNonNull(changeModeProcessing(schedule)));

        return processedScheduleList;

    }

    private List<Schedule> uniformMethodScheduleProcessing(Schedule responseSchedule) {
        ScheduleServiceModel schedule = new ScheduleServiceModel(responseSchedule);
        long fullWorkingTime = Math.abs(schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = schedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = countOnDay / minDuration;
        long periodDuration = fullWorkingTime * 60000 / countOfPeriod;
        List<Schedule> scheduleList = new ArrayList<>();

        Time startTime = schedule.getStartTime();
        for (int i = 0; i < countOfPeriod; i++) {
            scheduleList.add(new Schedule(schedule.getId(), new Time(startTime.getTime()).toString(), new Time(startTime.getTime() + periodDuration).toString(), schedule.getDate().toString(), minDuration));
            startTime.setTime(startTime.getTime() + periodDuration);
        }

        return scheduleList;

    }

    private List<Schedule> boundaryMethodScheduleProcessing(Schedule responseSchedule) {
        ScheduleServiceModel schedule = new ScheduleServiceModel(responseSchedule);
        long fullWorkingTime = Math.abs(schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = schedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = (countOnDay / minDuration) * 2;
        long periodDuration = fullWorkingTime * 60000 / countOfPeriod;
        List<Schedule> scheduleList = new ArrayList<>();

        Time startTime = schedule.getStartTime();
        for (int i = 0; i < countOfPeriod; i++) {
            if (i == 0 || i == countOfPeriod - 1) {
                scheduleList.add(new Schedule(schedule.getId(), new Time(startTime.getTime()).toString(), new Time(startTime.getTime() + periodDuration).toString(), schedule.getDate().toString(), minDuration * 3));
                startTime.setTime(startTime.getTime() + periodDuration * 3);
                continue;
            }
            scheduleList.add(new Schedule(schedule.getId(), new Time(startTime.getTime()).toString(), new Time(startTime.getTime() + periodDuration).toString(), schedule.getDate().toString(), minDuration));
            startTime.setTime(startTime.getTime() + periodDuration);
        }

        return scheduleList;

    }

    private List<Schedule> getEncodedScheduleList(List<Schedule> scheduleList) {
        List<Schedule> encodedScheduleList = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            encodedScheduleList.add(getEncodedSchedule(schedule));
        }
        return encodedScheduleList;

    }

    private Schedule getEncodedSchedule(Schedule scheduleServiceModel) {
        Schedule encodedSchedule = scheduleServiceModel.clone();
        encodedSchedule.setStartTime(encoder.encodeToString(encodedSchedule.getStartTime().getBytes(StandardCharsets.UTF_8)));
        encodedSchedule.setEndTime(encoder.encodeToString(encodedSchedule.getEndTime().getBytes(StandardCharsets.UTF_8)));
        encodedSchedule.setDate(encoder.encodeToString(encodedSchedule.getDate().getBytes(StandardCharsets.UTF_8)));
        return encodedSchedule;
    }

//    private List<Schedule> getDecodedScheduleList(List<Schedule> scheduleList) {
//        List<Schedule> decodedScheduleList = new ArrayList<>();
//        for (Schedule schedule : scheduleList) {
//            decodedScheduleList.add(getDecodedSchedule(schedule));
//        }
//        return decodedScheduleList;
//
//    }
    @Override
    public ScheduleDto getDecodedScheduleDto(ScheduleDto schedule) {
        schedule.setStartTime(new String(decoder.decode(schedule.getStartTime().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        schedule.setEndTime(new String(decoder.decode(schedule.getEndTime().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        schedule.setDate(new String(decoder.decode(schedule.getDate().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        return schedule;
    }


    private long getTimeWithNetwork(long time) {
        return (long) (time * Constants.DUTY_FACTOR);
    }

}
