package com.shedule.service;
import com.shedule.model.SheduleUser;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {

    List<SheduleUser> getAll();

    List<SheduleUser> getByRole(String role);

    SheduleUser getById(Integer id);

    SheduleUser create(String header) ;

    SheduleUser getByLoginAndPassword(String header);
}
