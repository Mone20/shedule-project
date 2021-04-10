package com.schedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.schedule.model.ScheduleUser;
import com.schedule.model.Views;
import com.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping()
    @JsonView(Views.Public.class)
    public ScheduleUser createUser(@RequestHeader("authorization") String header) {
        return userService.create(header);
    }

    @GetMapping("/get")
    @JsonView(Views.Public.class)
    public ScheduleUser getUser(@RequestHeader("authorization") String header) {
        return userService.getByLoginAndPassword(header);
    }
}
