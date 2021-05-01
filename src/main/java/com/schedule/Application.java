package com.schedule;

import com.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication



public class Application {
    @Autowired
    private static ScheduleService scheduleService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        scheduleService = (ScheduleService) context.getBean("scheduleServiceImpl");
        scheduleService.encryptAllSchedulesWithNewKey();

    }

    }

