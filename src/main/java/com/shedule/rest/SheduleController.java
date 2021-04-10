package com.shedule.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.shedule.model.Shedule;
import com.shedule.model.Views;
import com.shedule.rest.dto.SheduleDto;
import com.shedule.service.SheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shedules")
public class SheduleController {
    @Autowired
    SheduleService sheduleService;

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public List<Shedule> getAll() {
        return sheduleService.getAll();
    }

    @PostMapping()
    @JsonView(Views.Public.class)
    public List<Shedule> createShedule(@RequestBody SheduleDto body) {
        return sheduleService.create(body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration(),
                body.getUserId());
    }

    @PutMapping()
    @JsonView(Views.Public.class)
    public List<Shedule> updateShedule(@RequestBody SheduleDto body) {
        return sheduleService.update(body.getId(),
                body.getStartTime(),
                body.getEndTime(),
                body.getDate(),
                body.getDuration()
                );
    }
    @GetMapping("/{userId}")
    @JsonView(Views.Public.class)
    public List<Shedule> getByUser(@PathVariable String userId) {
        return sheduleService.getByUser(Integer.parseInt(userId));
    }

    @DeleteMapping("/{id}")
    public  void delete(@PathVariable String id){
        sheduleService.delete(Integer.parseInt(id));
    }
}
