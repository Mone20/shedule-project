package com.schedule.service.impl;

import com.schedule.Constants;
import com.schedule.model.Schedule;
import com.schedule.model.ScheduleServiceModel;
import com.schedule.repository.ScheduleRepository;
import com.schedule.rest.dto.ScheduleDto;
import com.schedule.service.ScheduleService;
import com.schedule.util.Aes256;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    final ScheduleRepository scheduleRepository;
    private final Aes256 aes256 = new Aes256();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }
    //Метод для перешифрования всх записей в таблице расписаний при перезапуске сервера
    @Override
    public void encryptAllSchedulesWithNewKey() {
        aes256.encryptAllRecordWithNewKey((List<Schedule>) scheduleRepository.findAll()).forEach(scheduleRepository::save);
    }
    //Метод для получения всех расписаний из БД
    @Override
    public List<Schedule> getAll() {
        return getEncodedScheduleList(aes256.decryptScheduleListCopy((List<Schedule>) scheduleRepository.findAll()));
    }
    //Метод для получения расписания по userId
    @Override
    public List<Schedule> getByUser(Integer userId) {
        List<Schedule> scheduleList = scheduleRepository.findByUserId(userId);
        return aes256.decryptScheduleListCopy(scheduleList);
    }
    //Метод для получения расписания по id
    @Override
    public Schedule getById(Integer id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        return getEncodedSchedule(aes256.decryptScheduleCopy(schedule));
    }
    //Метод для создания расписания
    @Override
    public List<Schedule> create(String startTime, String endTime, String date, Long duration, Integer userId, Integer mode) {
        SimpleDateFormat formatForDate = new SimpleDateFormat("yyyy-MM-dd");
        List<Schedule> schedules = scheduleRepository.findByDateAndUserId(aes256.encrypt(formatForDate.format(Date.valueOf(date))), userId);
        if(schedules != null && !schedules.isEmpty())
            return null;
        Schedule schedule = new Schedule(null,startTime, endTime, date, duration, userId, new Timestamp(System.currentTimeMillis()), null, mode);
        schedule = aes256.decryptSchedule(scheduleRepository.save(aes256.encryptScheduleCopy(schedule)));
        return changeModeProcessing(schedule);
    }
    //Метод для редактирования расписания для роли user
    @Override
    public List<Schedule> updateFromUser(Integer id, String startTime, String endTime, String date, Long duration, Integer mode) {
        Schedule schedule = aes256.decryptScheduleCopy(scheduleRepository.findById(id).get());
        java.util.Date dateNow = new java.util.Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        List<Schedule> schedules =  aes256.decryptScheduleList(scheduleRepository.findByDateAndUserId(aes256.encrypt(formatForDateNow.format(dateNow)), schedule.getUserId()));
        if(schedules != null && !schedules.isEmpty())
            return Collections.emptyList();

        return update(id, startTime, endTime,  date, duration,  mode);
    }
    //Метод для редактирования расписания для роли admin
    @Override
    public List<Schedule> update(Integer id, String startTime, String endTime, String date, Long duration, Integer mode){
        Schedule schedule = aes256.decryptScheduleCopy(scheduleRepository.findById(id).get());
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
    //Метод для получения расписанияя текущего дня для пользователя с userId
    @Override
    public List<Schedule> getCurrentByUser(Integer userId) {
        java.util.Date dateNow = new java.util.Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        List<Schedule> schedules = scheduleRepository.findByDateAndUserId(aes256.encrypt(formatForDateNow.format(dateNow)), userId);
        if (schedules == null || schedules.size() == 0) {
            dateNow.setTime(dateNow.getTime() - Constants.COUNT_MS_ON_DAY);
            schedules = scheduleRepository.findByDateAndUserId(aes256.encrypt(formatForDateNow.format(dateNow)), userId);
        }

        return listScheduleProcessing(aes256.decryptScheduleListCopy(schedules));

    }
    //Метод для удаления расписания для пользователя с роль user
    @Override
    public boolean deleteFromUser(Integer id) {
        java.util.Date dateNow = new java.util.Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        Schedule schedule = aes256.decryptScheduleCopy(scheduleRepository.findById(id).get());
        if(!schedule.getDate().equals(formatForDateNow.format(dateNow))) {
            delete(id);
            return true;
        }
        return false;
    }
    //Метод для удаления расписания для пользователя с роль admin
    @Override
    public void delete(Integer id) {
        scheduleRepository.deleteById(id);
    }

    //Метод для вызова обработки расписания исходя из выбранного режима обработки
    private List<Schedule> changeModeProcessing(Schedule schedule) {
        if (schedule.getMode() == Constants.UNIFORM_DISTRIBUTION)
            return getEncodedScheduleList(uniformMethodScheduleProcessing(schedule));
        if (schedule.getMode() == Constants.BOUNDARY_DISTRIBUTION)
            return getEncodedScheduleList(boundaryMethodScheduleProcessing(schedule));
        return null;

    }

    //Метод для обработки коллекции расписаний
    private List<Schedule> listScheduleProcessing(List<Schedule> scheduleList) {
        List<Schedule> processedScheduleList = new ArrayList<>();
        for (Schedule schedule : scheduleList)
            processedScheduleList.addAll(Objects.requireNonNull(changeModeProcessing(schedule)));

        return processedScheduleList;

    }
    //Метод для равномерной обработки расписания
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
            scheduleList.add(new Schedule(schedule.getId(),
                    new Time(startTime.getTime()).toString(),
                    new Time(startTime.getTime() + periodDuration).toString(),
                    schedule.getDate().toString(), minDuration, schedule.getUserId()));
            startTime.setTime(startTime.getTime() + periodDuration);
        }

        return scheduleList;

    }
    //Метод для пограничной обработки расписания
    private List<Schedule> boundaryMethodScheduleProcessing(Schedule responseSchedule) {
        ScheduleServiceModel schedule = new ScheduleServiceModel(responseSchedule);
        long fullWorkingTime = Math.abs(schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = schedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = (countOnDay / minDuration) - 4;
        List<Schedule> scheduleList = new ArrayList<>();
        Time startTime = schedule.getStartTime();
        if(countOfPeriod < 1)
            return Collections.emptyList();
        else if (countOfPeriod == 1) {
            return Collections.singletonList(new Schedule(schedule.getId(), new Time(startTime.getTime()).toString(),
                    new Time(schedule.getEndTime().getTime()).toString(), schedule.getDate().toString(),countOnDay , schedule.getUserId()));
        }
        long periodDuration = fullWorkingTime * 60000 / countOfPeriod;
        for (int i = 0; i < countOfPeriod; i++) {
            if (i == 0 || i == countOfPeriod - 1) {
                scheduleList.add(new Schedule(schedule.getId(), new Time(startTime.getTime()).toString(),
                        new Time(startTime.getTime() + periodDuration).toString(), schedule.getDate().toString(), minDuration * 3, schedule.getUserId()));

            } else {
            scheduleList.add(new Schedule(schedule.getId(), new Time(startTime.getTime()).toString(),
                    new Time(startTime.getTime() + periodDuration).toString(), schedule.getDate().toString(), minDuration, schedule.getUserId()));
        }
            startTime.setTime(startTime.getTime() + periodDuration);
}
        return scheduleList;

    }
    //Метод для кодирования коллекции расписаний
    private List<Schedule> getEncodedScheduleList(List<Schedule> scheduleList) {
        List<Schedule> encodedScheduleList = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            encodedScheduleList.add(getEncodedSchedule(schedule));
        }
        return encodedScheduleList;

    }
    //Метод для кодирования расписания
    private Schedule getEncodedSchedule(Schedule scheduleServiceModel) {
        Schedule encodedSchedule = scheduleServiceModel.clone();
        encodedSchedule.setId(scheduleServiceModel.getId());
        encodedSchedule.setUserId(scheduleServiceModel.getUserId());
        encodedSchedule.setStartTime(encoder.encodeToString(encodedSchedule.getStartTime().getBytes(StandardCharsets.UTF_8)));
        encodedSchedule.setEndTime(encoder.encodeToString(encodedSchedule.getEndTime().getBytes(StandardCharsets.UTF_8)));
        encodedSchedule.setDate(encoder.encodeToString(encodedSchedule.getDate().getBytes(StandardCharsets.UTF_8)));
        return encodedSchedule;
    }
    //Метод для кодирования DTO расписания
    @Override
    public ScheduleDto getDecodedScheduleDto(ScheduleDto schedule) {
        schedule.setStartTime(new String(decoder.decode(schedule.getStartTime().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        schedule.setEndTime(new String(decoder.decode(schedule.getEndTime().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        schedule.setDate(new String(decoder.decode(schedule.getDate().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        return schedule;
    }

    //Метод для получения разрешенного времени работы в интернете
    private long getTimeWithNetwork(long time) {
        return (long) (time * Constants.DUTY_FACTOR);
    }

}
