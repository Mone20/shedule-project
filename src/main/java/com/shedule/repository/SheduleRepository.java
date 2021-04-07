package com.shedule.repository;

import com.shedule.model.Shedule;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SheduleRepository extends CrudRepository<Shedule, Integer>, JpaSpecificationExecutor<Shedule> {
    Shedule findById(int id);
    List<Shedule> findByUserId(int id);
    void deleteById(Integer id);
}
