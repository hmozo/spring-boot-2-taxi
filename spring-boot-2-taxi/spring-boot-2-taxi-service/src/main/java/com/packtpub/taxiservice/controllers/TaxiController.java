package com.packtpub.taxiservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.packtpub.taximodel.dto.request.LocationDTO;
import com.packtpub.taximodel.dto.request.TaxiRegisterEventDTO;
import com.packtpub.taximodel.dto.response.TaxiAvailableResponseDTO;
import com.packtpub.taximodel.dto.response.TaxiLocationUpdatedEventResponseDTO;
import com.packtpub.taximodel.dto.response.TaxiRegisterEventResponseDTO;
import com.packtpub.taximodel.dto.response.TaxiStatusDTO;
import com.packtpub.taximodel.enums.TaxiStatus;
import com.packtpub.taximodel.enums.TaxiType;
import com.packtpub.taxiservice.services.TaxiService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/taxis")
public class TaxiController {

	private final TaxiService taxiService;
	
	public TaxiController(TaxiService taxiService) {
		this.taxiService= taxiService;
	}
	
	@GetMapping
	public Flux<TaxiAvailableResponseDTO> getAvailableTaxis(@RequestParam("type") TaxiType taxiType, @RequestParam("latitude") Double latitude, 
			@RequestParam("longitude") Double longitude, @RequestParam(value="radius", defaultValue = "1") Double radius){
		var availableTaxisFlux= taxiService.getAvailableTaxis(taxiType, latitude, longitude, radius);
		return availableTaxisFlux.map(r->new TaxiAvailableResponseDTO(r.getContent().getName()));
	}
	
	@GetMapping("/{taxiId}/status")
	public Mono<TaxiStatusDTO> getTaxiStatus(@PathVariable("taxiId")String taxiId){
		return taxiService.getTaxiStatus(taxiId).map(s->
			new TaxiStatusDTO(taxiId, s));
	}
	
	@PutMapping("/{taxiId}/status")
	public Mono<TaxiStatusDTO> updateTaxiStatus(@PathVariable("taxiId") String taxiId, @RequestParam("status") TaxiStatus taxiStatus){
		return taxiService.updateTaxiStatus(taxiId, taxiStatus).map(t-> new TaxiStatusDTO(t.getTaxiId(), t.getTaxiStatus()));
	}
	
	@PutMapping("/{taxiId}/location")
	public Mono<TaxiLocationUpdatedEventResponseDTO> updateLocation(@PathVariable("taxiId") String taxiId, @RequestBody LocationDTO locationDTO){
		return taxiService.updateLocation(taxiId, locationDTO).map(t-> new TaxiLocationUpdatedEventResponseDTO(taxiId));
	}
	
	@PostMapping
	public Mono<TaxiRegisterEventResponseDTO> register(@RequestBody TaxiRegisterEventDTO taxiRegisterEventDTO){
		return taxiService.register(taxiRegisterEventDTO).map(t-> new TaxiRegisterEventResponseDTO(t.getTaxiId()));
	}
}
