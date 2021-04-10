package com.schedule.repository;

import com.schedule.model.SheduleUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<SheduleUser, Integer> {
    SheduleUser findByLogin(String login);
    SheduleUser findByLoginAndPassword(String login, String password);
    boolean existsByLogin(String login);
    List<SheduleUser> findByRoleId(int id);
    void deleteById(Integer id);
}
