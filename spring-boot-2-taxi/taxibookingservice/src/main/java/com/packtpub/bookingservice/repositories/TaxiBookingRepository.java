package com.packtpub.bookingservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.packtpub.bookingservice.model.TaxiBooking;

@Repository
public interface TaxiBookingRepository extends CrudRepository<TaxiBooking, String>{

}
