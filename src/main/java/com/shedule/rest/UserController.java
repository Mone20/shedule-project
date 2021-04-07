package com.shedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.shedule.model.Shedule;
import com.shedule.model.SheduleUser;
import com.shedule.model.Views;
import com.shedule.rest.dto.SheduleDto;
import com.shedule.rest.dto.UserDto;
import com.shedule.service.SheduleService;
import com.shedule.service.UserService;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping()
    @JsonView(Views.Public.class)
    public SheduleUser createUser(@RequestHeader("authorization") String header) {
        return userService.create(header);
    }

    @GetMapping("/get")
    @JsonView(Views.Public.class)
    public SheduleUser getUser(@RequestHeader("authorization") String header) {
        return userService.getByLoginAndPassword(header);
    }
}
