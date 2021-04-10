package com.schedule.service.impl;

import com.schedule.model.Schedule;
import com.schedule.repository.ScheduleRepository;
import com.schedule.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    final ScheduleRepository scheduleRepository;
    private final double q = 0.1;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAll() {
        return (List<Schedule>) scheduleRepository.findAll();
    }

    @Override
    public List<Schedule> getByUser(Integer userId) {
        List<Schedule> scheduleList = scheduleRepository.findByUserId(userId);
        List<Schedule> processedScheduleList = new ArrayList<>();
        for(Schedule schedule : scheduleList)
            processedScheduleList.addAll(sheduleProcessing(schedule));

        return processedScheduleList;
    }

    @Override
    public Schedule getById(Integer id) {
        return scheduleRepository.findById(id).get();
    }

    @Override
    public List<Schedule> create(Time startTime, Time endTime, Date date, Long duration, Integer userId) {
        Schedule schedule = new Schedule(startTime, endTime, date, duration, userId, new Timestamp(System.currentTimeMillis()), null);
        scheduleRepository.save(schedule);
        return sheduleProcessing(schedule);
    }

    @Override
    public List<Schedule> update(Integer id, Time startTime, Time endTime, Date date, Long duration) {
        Schedule schedule = scheduleRepository.findById(id).get();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setDate(date);
        schedule.setDuration(duration);
        schedule.setModified(new Timestamp(System.currentTimeMillis()));
        scheduleRepository.save(schedule);
        return sheduleProcessing(schedule);
    }

    @Override
    public void delete(Integer id) {
        scheduleRepository.deleteById(id);
    }

    private List<Schedule> sheduleProcessing(Schedule schedule) {
        long fullWorkingTime = Math.abs(schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = schedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = countOnDay / minDuration;
        long periodDuration =fullWorkingTime*60000/countOfPeriod;
        List<Schedule> scheduleList = new ArrayList<>();
        Time startTime = schedule.getStartTime();
        for(int i = 0; i<countOfPeriod ; i++){
            scheduleList.add(new Schedule(schedule.getId(),new Time(startTime.getTime()),new Time(startTime.getTime()+ periodDuration), schedule.getDate(),minDuration));
            startTime.setTime(startTime.getTime()+periodDuration);
        }

        return scheduleList;

    }

    private long getTimeWithNetwork(long time){
        return time/10;
    }

}
