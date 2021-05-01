package com.schedule.model;

import com.fasterxml.jackson.annotation.JsonView;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class ScheduleServiceModel {
    @JsonView(Views.Private.class)
    private Integer id;
    @JsonView(Views.Public.class)
    private Time startTime;
    @JsonView(Views.Public.class)
    private Time endTime;
    @JsonView(Views.Public.class)
    private Date date;
    @JsonView(Views.Public.class)
    private Long duration;
    @JsonView(Views.Private.class)
    private Integer userId;
    @JsonView(Views.Private.class)
    private Integer mode;
    private Timestamp created;
    private Timestamp modified;
    public ScheduleServiceModel(Integer id,
                                Time startTime,
                                Time endTime,
                                Date date,
                    Long duration) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.duration = duration;
    }

    public ScheduleServiceModel(Schedule schedule) {
        this.startTime = Time.valueOf(schedule.getStartTime());
        this.endTime = Time.valueOf(schedule.getEndTime());
        this.date = Date.valueOf(schedule.getDate());
        this.duration = schedule.getDuration();
        this.userId = schedule.getUserId();
        this.created = schedule.getCreated();
        this.modified = schedule.getModified();
        this.mode = schedule.getMode();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}
