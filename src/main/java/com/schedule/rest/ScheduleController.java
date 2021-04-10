package com.schedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.schedule.model.Schedule;
import com.schedule.model.Views;
import com.schedule.rest.dto.ScheduleDto;
import com.schedule.service.ScheduleService;
import com.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    UserService userService;

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<Schedule> getAll(@RequestHeader("authorization") String header) {
        if(userService.authorization(header))
        return scheduleService.getAll();
        return null;
    }

    @PostMapping()
    @JsonView(Views.Public.class)
    public List<Schedule> createSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header) {
        if(userService.authorization(header))
        return scheduleService.create(body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration(),
                body.getUserId());
        return null;
    }

    @PutMapping()
    @JsonView(Views.Public.class)
    public List<Schedule> updateSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header) {
        if(userService.authorization(header))
        return scheduleService.update(body.getId(),
                body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration()
                );
        return null;
    }
    @GetMapping("/{userId}")
    @JsonView(Views.Public.class)
    public List<Schedule> getByUser(@PathVariable String userId, @RequestHeader("authorization") String header) {
        if(userService.authorization(header))
        return scheduleService.getByUser(Integer.parseInt(userId));
        return null;
    }

    @DeleteMapping("/{id}")
    public  void delete(@PathVariable String id, @RequestHeader("authorization") String header){
        if(userService.authorization(header))
        scheduleService.delete(Integer.parseInt(id));

    }
}
