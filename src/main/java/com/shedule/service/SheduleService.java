package com.shedule.service;

import com.shedule.model.Shedule;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Service
public interface SheduleService {

    List<Shedule> getAll();

    List<Shedule> getByUser(Integer userId);

    Shedule getById(Integer id);

    List<Shedule> create(Time startTime,
                   Time endTime,
                   Date date,
                   Long duration,
                   Integer userId);
    List<Shedule> update(Integer id,
                   Time startTime,
                   Time endTime,
                   Date date,
                   Long duration);

    void delete(Integer id);


}
