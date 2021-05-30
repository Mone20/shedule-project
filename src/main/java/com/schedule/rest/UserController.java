package com.schedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.schedule.Constants;
import com.schedule.model.ScheduleUser;
import com.schedule.model.Views;
import com.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;
    //API для регистрации пользователя
    @GetMapping()
    @JsonView(Views.Public.class)
    public ScheduleUser createUser(@RequestHeader("authorization") String header) {
        return userService.create(header);
    }
    //API для прохождения авторизации
    @GetMapping("/get")
    @JsonView(Views.Public.class)
    public ScheduleUser getUser(@RequestHeader("authorization") String header) {
        return userService.getByLoginAndPassword(header);
    }
    //API для получения всех пользователей
    @GetMapping("/all")
    @JsonView(Views.Private.class)
    public List<ScheduleUser> getAllUsers(@RequestHeader("authorization") String header) {
        if(userService.authorization(header).getRole().getName().equals(Constants.ROLES.ADMIN))
        return userService.getAll();
        return null;
    }
}
