package com.shedule.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Lazy
@Entity
@Data
public class Shedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private Integer id;
    @JsonView(Views.Public.class)
    private Time startTime;
    @JsonView(Views.Public.class)
    private Time endTime;
    @JsonView(Views.Public.class)
    private Date date;
    @JsonView(Views.Public.class)
    private Long duration;
    @JsonView(Views.Internal.class)
    private Integer userId;
    private Timestamp created;
    private Timestamp modified;

    public Shedule(Time startTime,
                   Time endTime,
                   Date date,
                   Long duration,
                   Integer userId,
                   Timestamp created,
                   Timestamp modified) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.duration = duration;
        this.userId = userId;
        this.created = created;
        this.modified = modified;
    }

    public Shedule() {
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
}
