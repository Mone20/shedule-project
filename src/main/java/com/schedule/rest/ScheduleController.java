package com.schedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.schedule.Constants;
import com.schedule.model.Schedule;
import com.schedule.model.ScheduleUser;
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
    @JsonView(Views.Private.class)
    public List<Schedule> getAll(@RequestHeader("authorization") String header) {
        if(userService.authorization(header).equals(Constants.ROLES.ADMIN))
        return scheduleService.getAll();
        return null;
    }

    @PostMapping()
    @JsonView(Views.Public.class)
    public List<Schedule> createSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header) {
        body = scheduleService.getDecodedScheduleDto(body);
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN) || user.getRole().getName().equals(Constants.ROLES.USER))
        return scheduleService.create(body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration(),
                body.getUserId(),
                body.getMode());
        return null;
    }

    @PutMapping("/{id}")
    @JsonView(Views.Public.class)
    public List<Schedule> updateSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header, @PathVariable String id) {
        body = scheduleService.getDecodedScheduleDto(body);
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN))
        return scheduleService.update(Integer.parseInt(id),
                body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration(),
                body.getMode());
        else if(user.getRole().getName().equals(Constants.ROLES.USER)
                && scheduleService.getById(Integer.parseInt(id)).get(0).getUserId().equals(user.getId()))
            return scheduleService.updateFromUser(Integer.parseInt(id),
                    body.getStartTime(),
                    body.getEndTime(),
                    body.getDate(),
                    body.getDuration(),
                    body.getMode());
        return null;
    }
    @GetMapping("/{userId}")
    @JsonView(Views.Public.class)
    public List<Schedule> getByUser(@PathVariable String userId, @RequestHeader("authorization") String header) {
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN) || user.getRole().getName().equals(Constants.ROLES.USER))
        return scheduleService.getByUser(Integer.parseInt(userId));
        return null;
    }
    @GetMapping("/current/{userId}")
    @JsonView(Views.Public.class)
    public List<Schedule> getCurrentByUser(@PathVariable String userId, @RequestHeader("authorization") String header) {
        ScheduleUser user = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN) || user.getRole().getName().equals(Constants.ROLES.USER))
            return scheduleService.getCurrentByUser(Integer.parseInt(userId));
        return null;
    }

    @DeleteMapping("/{id}")
    public  void delete(@PathVariable String id, @RequestHeader("authorization") String header){
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN))
        scheduleService.delete(Integer.parseInt(id));
        else if(user.getRole().getName().equals(Constants.ROLES.USER)
                && scheduleService.getById(Integer.parseInt(id)).get(0).getUserId().equals(user.getId()))
            scheduleService.deleteFromUser(Integer.parseInt(id));

    }
}
