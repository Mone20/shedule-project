package com.schedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.schedule.Constants;
import com.schedule.model.Schedule;
import com.schedule.model.ScheduleServiceModel;
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
    public List<ScheduleServiceModel> createSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header) {
        String role = userService.authorization(header);
        if(role.equals(Constants.ROLES.ADMIN) || role.equals(Constants.ROLES.USER))
        return scheduleService.create(body.getStartTime().toString(),
                body.getEndTime().toString(),
                body.getDate().toString(),
                body.getDuration(),
                body.getUserId(),
                body.getMode());
        return null;
    }

    @PutMapping("/{id}")
    @JsonView(Views.Public.class)
    public List<ScheduleServiceModel> updateSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header, @PathVariable String id) {
        String role = userService.authorization(header);
        if(role.equals(Constants.ROLES.ADMIN) || role.equals(Constants.ROLES.USER))
        return scheduleService.update(Integer.parseInt(id),
                body.getStartTime().toString(),
                body.getEndTime().toString(),
                body.getDate().toString(),
                body.getDuration(),
                body.getMode());
        return null;
    }
    @GetMapping("/{userId}")
    @JsonView(Views.Public.class)
    public List<ScheduleServiceModel> getByUser(@PathVariable String userId, @RequestHeader("authorization") String header) {
        String role = userService.authorization(header);
        if(role.equals(Constants.ROLES.ADMIN) || role.equals(Constants.ROLES.USER))
        return scheduleService.getByUser(Integer.parseInt(userId));
        return null;
    }
    @GetMapping("/current/{userId}")
    @JsonView(Views.Public.class)
    public List<ScheduleServiceModel> getCurrentByUser(@PathVariable String userId, @RequestHeader("authorization") String header) {
        String role = userService.authorization(header);
        if(role.equals(Constants.ROLES.ADMIN) || role.equals(Constants.ROLES.USER))
            return scheduleService.getCurrentByUser(Integer.parseInt(userId));
        return null;
    }

    @DeleteMapping("/{id}")
    public  void delete(@PathVariable String id, @RequestHeader("authorization") String header){
        String role = userService.authorization(header);
        if(role.equals(Constants.ROLES.ADMIN) || role.equals(Constants.ROLES.USER))
        scheduleService.delete(Integer.parseInt(id));

    }
}
