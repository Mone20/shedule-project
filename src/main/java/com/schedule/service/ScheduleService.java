package com.schedule.service;

import com.schedule.model.Schedule;
import com.schedule.model.ScheduleServiceModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleService {

    void encryptAllSchedulesWithNewKey();

    List<Schedule> getAll();

    List<ScheduleServiceModel> getByUser(Integer userId);

    List<ScheduleServiceModel> getCurrentByUser(Integer userId);

    List<ScheduleServiceModel> getById(Integer id);

    List<ScheduleServiceModel> create(String startTime,
                                      String endTime,
                                      String date,
                                      Long duration,
                                      Integer userId,
                                      Integer mode);
    List<ScheduleServiceModel> update(Integer id,
                                      String startTime,
                                      String endTime,
                                      String date,
                                      Long duration,
                                      Integer mode);

    void delete(Integer id);

}
