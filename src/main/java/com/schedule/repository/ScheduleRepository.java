package com.schedule.repository;

import com.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Integer>, JpaSpecificationExecutor<Schedule> {
    Schedule findById(int id);
    List<Schedule> findByUserId(int id);
    void deleteById(Integer id);

}
