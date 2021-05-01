package com.schedule.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;


@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JsonView(Views.Public.class)
    private String name;
    @OneToMany
    private List<ScheduleUser> users;

    public Role() {
    }
    public Role(Integer id , String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScheduleUser> getUsers() {
        return users;
    }

    public void setUsers(List<ScheduleUser> users) {
        this.users = users;
    }
}
