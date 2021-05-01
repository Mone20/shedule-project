package com.schedule.service.impl;

import com.schedule.Constants;
import com.schedule.model.Schedule;
import com.schedule.model.ScheduleServiceModel;
import com.schedule.repository.ScheduleRepository;
import com.schedule.service.ScheduleService;
import com.schedule.util.Aes256;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    final ScheduleRepository scheduleRepository;
    private final Aes256 aes256 = new Aes256();

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void encryptAllSchedulesWithNewKey() {
        aes256.encryptAllRecordWithNewKey((List<Schedule>) scheduleRepository.findAll()).forEach(scheduleRepository::save);
    }

    @Override
    public List<Schedule> getAll() {
        return aes256.decryptScheduleListCopy((List<Schedule>) scheduleRepository.findAll());
    }

    @Override
    public List<ScheduleServiceModel> getByUser(Integer userId) {
        List<Schedule> scheduleList = scheduleRepository.findByUserId(userId);
        return listScheduleProcessing(aes256.decryptScheduleListCopy(scheduleList));
    }

    @Override
    public List<ScheduleServiceModel> getById(Integer id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        return changeModeProcessing(aes256.decryptScheduleCopy(schedule));
    }

    @Override
    public List<ScheduleServiceModel> create(String startTime, String endTime, String date, Long duration, Integer userId, Integer mode) {
        Schedule schedule = new Schedule(startTime, endTime, date, duration, userId, new Timestamp(System.currentTimeMillis()), null, mode);
        scheduleRepository.save(aes256.encryptScheduleCopy(schedule));
        return changeModeProcessing(schedule);
    }

    @Override
    public List<ScheduleServiceModel> update(Integer id, String startTime, String endTime, String date, Long duration, Integer mode) {
        Schedule schedule = aes256.decryptScheduleCopy(scheduleRepository.findById(id).get());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setDate(date);
        schedule.setDuration(duration);
        schedule.setModified(new Timestamp(System.currentTimeMillis()));
        schedule.setMode(mode);
        scheduleRepository.save(aes256.encryptScheduleCopy(schedule));
        return changeModeProcessing(schedule);
    }

    @Override
    public List<ScheduleServiceModel> getCurrentByUser(Integer userId) {
        java.util.Date dateNow = new java.util.Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        List<Schedule> schedules = scheduleRepository.findByDateAndUserId(Date.valueOf(formatForDateNow.format(dateNow)), userId);

        return listScheduleProcessing(aes256.decryptScheduleListCopy(schedules));

    }

    @Override
    public void delete(Integer id) {
        scheduleRepository.deleteById(id);
    }


    private List<ScheduleServiceModel> changeModeProcessing(Schedule schedule) {
        if (schedule.getMode() == Constants.UNIFORM_DISTRIBUTION)
            return uniformMethodScheduleProcessing(schedule);
        if (schedule.getMode() == Constants.BOUNDARY_DISTRIBUTION)
            return boundaryMethodScheduleProcessing(schedule);
        return null;

    }


    private List<ScheduleServiceModel> listScheduleProcessing(List<Schedule> scheduleList) {
        List<ScheduleServiceModel> processedScheduleList = new ArrayList<>();
        for (Schedule schedule : scheduleList)
            processedScheduleList.addAll(Objects.requireNonNull(changeModeProcessing(schedule)));

        return processedScheduleList;

    }

    private List<ScheduleServiceModel> uniformMethodScheduleProcessing(Schedule responseSchedule) {
        ScheduleServiceModel schedule = new ScheduleServiceModel(responseSchedule);
        long fullWorkingTime = Math.abs(schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = schedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = countOnDay / minDuration;
        long periodDuration = fullWorkingTime * 60000 / countOfPeriod;
        List<ScheduleServiceModel> scheduleList = new ArrayList<>();

        Time startTime = schedule.getStartTime();
        for (int i = 0; i < countOfPeriod; i++) {
            scheduleList.add(new ScheduleServiceModel(schedule.getId(), new Time(startTime.getTime()), new Time(startTime.getTime() + periodDuration), schedule.getDate(), minDuration));
            startTime.setTime(startTime.getTime() + periodDuration);
        }

        return scheduleList;

    }

    private List<ScheduleServiceModel> boundaryMethodScheduleProcessing(Schedule responseSchedule) {
        ScheduleServiceModel schedule = new ScheduleServiceModel(responseSchedule);
        long fullWorkingTime = Math.abs(schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = schedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = (countOnDay / minDuration) * 2;
        long periodDuration = fullWorkingTime * 60000 / countOfPeriod;
        List<ScheduleServiceModel> scheduleList = new ArrayList<>();

        Time startTime = schedule.getStartTime();
        for (int i = 0; i < countOfPeriod; i++) {
            if (i == 0 || i == countOfPeriod - 1) {
                scheduleList.add(new ScheduleServiceModel(schedule.getId(), new Time(startTime.getTime()), new Time(startTime.getTime() + periodDuration), schedule.getDate(), minDuration * 3));
                startTime.setTime(startTime.getTime() + periodDuration * 3);
                continue;
            }
            scheduleList.add(new ScheduleServiceModel(schedule.getId(), new Time(startTime.getTime()), new Time(startTime.getTime() + periodDuration), schedule.getDate(), minDuration));
            startTime.setTime(startTime.getTime() + periodDuration);
        }

        return scheduleList;

    }


    private long getTimeWithNetwork(long time) {
        return (long) (time * Constants.DUTY_FACTOR);
    }

}
