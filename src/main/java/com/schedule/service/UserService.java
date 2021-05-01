package com.schedule.service;
import com.schedule.model.ScheduleUser;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {

    List<ScheduleUser> getAll();

    List<ScheduleUser> getByRole(String role);

    ScheduleUser getById(Integer id);

    ScheduleUser create(String header) ;

    ScheduleUser getByLoginAndPassword(String header);

    String authorization(String header);
}
