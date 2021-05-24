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

    //    private List<Schedule> getDecodedScheduleList(List<Schedule> scheduleList) {
    //        List<Schedule> decodedScheduleList = new ArrayList<>();
    //        for (Schedule schedule : scheduleList) {
    //            decodedScheduleList.add(getDecodedSchedule(schedule));
    //        }
    //        return decodedScheduleList;
    //
    //    }
    ScheduleDto getDecodedScheduleDto(ScheduleDto schedule);
}
