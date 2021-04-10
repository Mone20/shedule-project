package com.schedule.model;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;


@Entity
public class ScheduleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private Integer id;
    private String login;
    @JsonView(Views.Private.class)
    private String password;
    @ManyToOne
    @JsonView(Views.Public.class)
    private Role role;

    public ScheduleUser() {
    }
    public ScheduleUser(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
