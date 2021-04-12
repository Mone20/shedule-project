package com.schedule.rest.dto;

import java.sql.Date;
import java.sql.Time;
public class ScheduleDto {
    private Integer id;
    private Time startTime;
    private Time endTime;
    private Long duration;
    private Date date;
    private Integer userId;


    private Integer mode;

    public ScheduleDto(Integer id, Time startTime, Time endTime, Long duration, Date date, Integer userId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.date = date;
        this.userId = userId;
    }

    public ScheduleDto() {
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}
