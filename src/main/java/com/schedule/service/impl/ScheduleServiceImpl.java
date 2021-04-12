package com.schedule.service.impl;

import com.schedule.Constants;
import com.schedule.model.Schedule;
import com.schedule.repository.ScheduleRepository;
import com.schedule.service.ScheduleService;
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

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAll() {
        return (List<Schedule>) scheduleRepository.findAll();
    }

    @Override
    public List<Schedule> getByUser(Integer userId, Integer mode) {
        List<Schedule> scheduleList = scheduleRepository.findByUserId(userId);
        return listScheduleProcessing(scheduleList, mode);
    }

    @Override
    public List<Schedule> getById(Integer id, Integer mode) {
        Schedule schedule = scheduleRepository.findById(id).get();
        return changeModeProcessing(schedule, mode);
    }

    @Override
    public List<Schedule> create(Time startTime, Time endTime, Date date, Long duration, Integer userId, Integer mode) {
        Schedule schedule = new Schedule(startTime, endTime, date, duration, userId, new Timestamp(System.currentTimeMillis()), null);
        scheduleRepository.save(schedule);
        return changeModeProcessing(schedule, mode);
    }

    @Override
    public List<Schedule> update(Integer id, Time startTime, Time endTime, Date date, Long duration, Integer mode) {
        Schedule schedule = scheduleRepository.findById(id).get();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setDate(date);
        schedule.setDuration(duration);
        schedule.setModified(new Timestamp(System.currentTimeMillis()));
        scheduleRepository.save(schedule);
        return changeModeProcessing(schedule, mode);
    }

    @Override
    public List<Schedule> getCurrentByUser(Integer userId, Integer mode) {
        java.util.Date dateNow = new java.util.Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        List<Schedule> schedules = scheduleRepository.findByDateAndUserId(Date.valueOf(formatForDateNow.format(dateNow)), userId);

        return listScheduleProcessing(schedules,mode);

    }

    @Override
    public void delete(Integer id) {
        scheduleRepository.deleteById(id);
    }


    private List<Schedule> changeModeProcessing(Schedule schedule, Integer mode) {
        if (mode == Constants.UNIFORM_DISTRIBUTION)
            return scheduleProcessing(schedule);
        if (mode == Constants.BOUNDARY_DISTRIBUTION){}
        //TODO
        return null;

    }


    private List<Schedule> listScheduleProcessing(List<Schedule> scheduleList, Integer mode) {
        List<Schedule> processedScheduleList = new ArrayList<>();
        for (Schedule schedule : scheduleList)
            processedScheduleList.addAll(Objects.requireNonNull(changeModeProcessing(schedule, mode)));

        return processedScheduleList;

    }

    private List<Schedule> scheduleProcessing(Schedule schedule) {
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
            scheduleList.add(new Schedule(schedule.getId(), new Time(startTime.getTime()), new Time(startTime.getTime() + periodDuration), schedule.getDate(), minDuration));
            startTime.setTime(startTime.getTime() + periodDuration);
        }

        return scheduleList;

    }

    private long getTimeWithNetwork(long time) {
        return time / 10;
    }

}
