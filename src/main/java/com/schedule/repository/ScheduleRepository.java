package com.schedule.repository;

import com.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Integer>, JpaSpecificationExecutor<Schedule> {
    List<Schedule> findById(int id);
    List<Schedule> findByDateAndUserId(Date date,int id);
    List<Schedule> findByUserId(int id);
    void deleteById(Integer id);

}
