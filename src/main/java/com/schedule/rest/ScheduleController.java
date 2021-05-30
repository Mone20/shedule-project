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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
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
    public ResponseEntity<List<Schedule>> updateSchedule(@RequestBody ScheduleDto body, @RequestHeader("authorization") String header, @PathVariable String id) {
        body = scheduleService.getDecodedScheduleDto(body);
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN))
        return new ResponseEntity(scheduleService.update(Integer.parseInt(id),
                body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration(),
                body.getMode()),HttpStatus.OK);
        else if(user.getRole().getName().equals(Constants.ROLES.USER)
                && scheduleService.getById(Integer.parseInt(id)).getUserId().equals(user.getId())) {
            List<Schedule> updatedList = scheduleService.updateFromUser(Integer.parseInt(id),
                    body.getStartTime(),
                    body.getEndTime(),
                    body.getDate(),
                    body.getDuration(),
            body.getMode());
            if(updatedList.isEmpty())
            return new ResponseEntity(updatedList,HttpStatus.INTERNAL_SERVER_ERROR);
            else
                return new ResponseEntity(updatedList,HttpStatus.OK);
        }
        return new ResponseEntity(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @GetMapping("/user/{userId}")
    @JsonView(Views.Public.class)
    public List<Schedule> getByUser(@PathVariable String userId, @RequestHeader("authorization") String header) {
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN) || user.getRole().getName().equals(Constants.ROLES.USER))
        return scheduleService.getByUser(Integer.parseInt(userId));
        return null;
    }

    @GetMapping("/{scheduleId}")
    @JsonView(Views.Private.class)
    public Schedule getById(@PathVariable String scheduleId, @RequestHeader("authorization") String header) {
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN) || (user.getRole().getName().equals(Constants.ROLES.USER)
                && scheduleService.getById(Integer.parseInt(scheduleId)).getUserId().equals(user.getId())))
             return scheduleService.getById(Integer.parseInt(scheduleId));

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
    public ResponseEntity delete(@PathVariable String id, @RequestHeader("authorization") String header){
        ScheduleUser user  = userService.authorization(header);
        if(user.getRole().getName().equals(Constants.ROLES.ADMIN)) {
            scheduleService.delete(Integer.parseInt(id));
            return  new ResponseEntity(HttpStatus.OK);
        }
        else if(user.getRole().getName().equals(Constants.ROLES.USER)
                && scheduleService.getById(Integer.parseInt(id)).getUserId().equals(user.getId())) {

            if(scheduleService.deleteFromUser(Integer.parseInt(id)))
                return new ResponseEntity(HttpStatus.OK);
            else
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
