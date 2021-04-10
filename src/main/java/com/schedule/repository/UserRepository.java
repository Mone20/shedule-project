package com.schedule.repository;

import com.schedule.model.ScheduleUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<ScheduleUser, Integer> {
    ScheduleUser findByLogin(String login);
    ScheduleUser findByLoginAndPassword(String login, String password);
    boolean existsByLogin(String login);
    List<ScheduleUser> findByRoleId(int id);
    void deleteById(Integer id);
}
