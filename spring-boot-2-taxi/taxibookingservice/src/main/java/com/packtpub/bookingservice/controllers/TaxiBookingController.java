package com.packtpub.bookingservice.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.packtpub.bookingservice.services.TaxiBookingService;
import com.packtpub.taximodel.dto.request.TaxiBookedEventDTO;
import com.packtpub.taximodel.dto.request.TaxiBookingAcceptedEventDTO;
import com.packtpub.taximodel.dto.response.TaxiBookedEventResponseDTO;
import com.packtpub.taximodel.dto.response.TaxiBookingAcceptedEventResponseDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/taxibookings")
public class TaxiBookingController {
	private final TaxiBookingService taxiBookingService;
	
	public TaxiBookingController(TaxiBookingService taxiBookingService) {
		this.taxiBookingService= taxiBookingService;
	}
	
	@PostMapping
	public Mono<TaxiBookedEventResponseDTO> book(@RequestBody TaxiBookedEventDTO taxiBookedEventDTO){
		return taxiBookingService.book(taxiBookedEventDTO).map(t->new TaxiBookedEventResponseDTO(t.getTaxiBookingId()));
	}
	
	@PutMapping("/{taxiBookingId}/accept")
	public Mono<TaxiBookingAcceptedEventResponseDTO> accept(@PathVariable("taxiBookingId") String taxiBookingId, @RequestBody TaxiBookingAcceptedEventDTO taxiBookingAcceptedEventDTO){
		return taxiBookingService.accept(taxiBookingId, taxiBookingAcceptedEventDTO).map(t-> new TaxiBookingAcceptedEventResponseDTO(t.getTaxiBookingId(), t.getTaxiId(), t.getAcceptedTime()));
	}
	
}
