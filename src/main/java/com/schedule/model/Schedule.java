package com.schedule.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.sql.Timestamp;

@Lazy
@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private Integer id;
    @JsonView(Views.Public.class)
    private String startTime;
    @JsonView(Views.Public.class)
    private String endTime;
    @JsonView(Views.Public.class)
    private String date;
    @JsonView(Views.Public.class)
    private Long duration;
    @JsonView(Views.Private.class)
    private Integer userId;
    @JsonView(Views.Private.class)
    private Integer mode;
    private Timestamp created;
    private Timestamp modified;
    @JsonView(Views.Public.class)
    private String hashCode;

    public Schedule( Integer id,
            String startTime,
                    String endTime,
                    String date,
                    Long duration,
                    Integer userId,
                    Timestamp created,
                    Timestamp modified,
                    Integer mode) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.duration = duration;
        this.userId = userId;
        this.created = created;
        this.modified = modified;
        this.mode = mode;
    }
    public Schedule  (Integer id,
                                String startTime,
                               String endTime,
                               String date,
                                Long duration,
                      Integer userId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.duration = duration;
        this.userId = userId;
    }
    public Schedule() {
    }

    public Schedule(ScheduleServiceModel scheduleServiceModel){
        this.startTime = scheduleServiceModel.getStartTime().toString();
        this.endTime = scheduleServiceModel.getEndTime().toString();
        this.date = scheduleServiceModel.getDate().toString();
        this.duration = scheduleServiceModel.getDuration();
        this.userId = scheduleServiceModel.getUserId();
        this.created = scheduleServiceModel.getCreated();
        this.modified = scheduleServiceModel.getModified();
        this.mode = scheduleServiceModel.getMode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode() {
        this.hashCode = DigestUtils.md5Hex(String.valueOf(id)+startTime+endTime+date+duration);
    }

    @Override
    public Schedule clone() {
        return new Schedule(this.id,
                this.startTime,
                this.endTime,
                this.date,
                this.duration,
                this.userId,
                this.created,
                this.modified,
                this.mode);
    }
}
