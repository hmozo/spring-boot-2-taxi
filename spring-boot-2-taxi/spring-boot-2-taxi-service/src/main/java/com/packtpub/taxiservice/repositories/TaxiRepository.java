package com.packtpub.taxiservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.packtpub.taxiservice.model.Taxi;

@Repository
public interface TaxiRepository extends CrudRepository<Taxi, String>{

}
