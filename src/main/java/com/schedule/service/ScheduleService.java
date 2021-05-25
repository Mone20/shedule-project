package com.schedule.service;

import com.schedule.model.Schedule;
import com.schedule.rest.dto.ScheduleDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleService {

    void encryptAllSchedulesWithNewKey();

    List<Schedule> getAll();

    List<Schedule> getByUser(Integer userId);

    List<Schedule> updateFromUser(Integer id, String startTime, String endTime, String date, Long duration, Integer mode);


    List<Schedule> getCurrentByUser(Integer userId);

    List<Schedule> getById(Integer id);

    List<Schedule> create(String startTime,
                          String endTime,
                          String date,
                          Long duration,
                          Integer userId,
                          Integer mode);
    List<Schedule> update(Integer id,
                          String startTime,
                          String endTime,
                          String date,
                          Long duration,
                          Integer mode);

    void delete(Integer id);

    void deleteFromUser(Integer id);

    ScheduleDto getDecodedScheduleDto(ScheduleDto schedule);
}
