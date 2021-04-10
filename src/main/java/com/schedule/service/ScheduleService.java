package com.schedule.service;

import com.schedule.model.Schedule;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Service
public interface ScheduleService {

    List<Schedule> getAll();

    List<Schedule> getByUser(Integer userId);

    List<Schedule> getCurrentByUser(Integer userId);

    Schedule getById(Integer id);

    List<Schedule> create(Time startTime,
                          Time endTime,
                          Date date,
                          Long duration,
                          Integer userId);
    List<Schedule> update(Integer id,
                          Time startTime,
                          Time endTime,
                          Date date,
                          Long duration);

    void delete(Integer id);


}
