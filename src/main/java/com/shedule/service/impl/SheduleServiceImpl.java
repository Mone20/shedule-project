package com.shedule.service.impl;

import com.shedule.model.Shedule;
import com.shedule.repository.SheduleRepository;
import com.shedule.service.SheduleService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class SheduleServiceImpl implements SheduleService {
    final SheduleRepository sheduleRepository;
    private final double q = 0.1;

    public SheduleServiceImpl(SheduleRepository sheduleRepository) {
        this.sheduleRepository = sheduleRepository;
    }

    @Override
    public List<Shedule> getAll() {
        return (List<Shedule>) sheduleRepository.findAll();
    }

    @Override
    public List<Shedule> getByUser(Integer userId) {
        return sheduleProcessing(sheduleRepository.findByUserId(userId).get(0));
    }

    @Override
    public Shedule getById(Integer id) {
        return sheduleRepository.findById(id).get();
    }

    @Override
    public Shedule create(Time startTime, Time endTime, Date date, Long duration, Integer userId) {
        Shedule shedule = new Shedule(startTime, endTime, date, duration, userId, new Timestamp(System.currentTimeMillis()), null);
        return sheduleRepository.save(shedule);
    }

    @Override
    public Shedule update(Integer id, Time startTime, Time endTime, Date date, Long duration) {
        Shedule shedule = sheduleRepository.findById(id).get();
        shedule.setStartTime(startTime);
        shedule.setEndTime(endTime);
        shedule.setDate(date);
        shedule.setDuration(duration);
        shedule.setModified(new Timestamp(System.currentTimeMillis()));
        sheduleRepository.save(shedule);
        return shedule;
    }

    @Override
    public void delete(Integer id) {
        sheduleRepository.deleteById(id);
    }

    private List<Shedule> sheduleProcessing(Shedule shedule) {
        long fullWorkingTime = Math.abs(shedule.getEndTime().getTime() - shedule.getStartTime().getTime()) / 60000;
        long countOnDay = getTimeWithNetwork(fullWorkingTime);
        long minDuration = shedule.getDuration();
        while (countOnDay % minDuration != 0)
            minDuration++;
        long countOfPeriod = countOnDay / minDuration;
        long periodDuration =fullWorkingTime*60000/countOfPeriod;
        List<Shedule> sheduleList = new ArrayList<>();
        Time startTime = shedule.getStartTime();
        for(int i = 0; i<countOfPeriod ; i++){
            sheduleList.add(new Shedule(new Time(startTime.getTime()),new Time(startTime.getTime()+ periodDuration), shedule.getDate(),minDuration,null,null,null));
            startTime.setTime(startTime.getTime()+periodDuration);
        }

        return sheduleList;

    }

    private long getTimeWithNetwork(long time){
        return time/10;
    }

}
