package com.packtpub.taxiservice.services;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import com.packtpub.taximodel.converter.LocationToPointConverter;
import com.packtpub.taximodel.dto.request.LocationDTO;
import com.packtpub.taximodel.dto.request.TaxiRegisterEventDTO;
import com.packtpub.taximodel.enums.TaxiStatus;
import com.packtpub.taximodel.enums.TaxiType;
import com.packtpub.taxiservice.exceptions.TaxiIdNotFoundException;
import com.packtpub.taxiservice.model.Taxi;
import com.packtpub.taxiservice.repositories.TaxiRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TaxiService {
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final TaxiRepository taxiRepository;
	private final LocationToPointConverter locationToPointConverter= new LocationToPointConverter();
	
	public TaxiService(ReactiveRedisTemplate<String, String> reactiveRedisTemplate, TaxiRepository taxiRepository) {
		this.reactiveRedisTemplate= reactiveRedisTemplate;
		this.taxiRepository= taxiRepository;
	}
	
	public Mono<Taxi> register(TaxiRegisterEventDTO taxiRegisterEventDTO){
		Taxi taxi= new Taxi(taxiRegisterEventDTO.getTaxiID(), taxiRegisterEventDTO.getTaxiType(), TaxiStatus.AVAILABLE);
		return Mono.just(taxiRepository.save(taxi));
	}
	
	public Mono<Taxi> updateLocation(String taxiId, LocationDTO locationDTO){
		Optional<Taxi> taxiOptional= taxiRepository.findById(taxiId);
		
		if(!taxiOptional.isPresent()) {
			throw getTaxiIdNotFoundException(taxiId);
		}
		
		Taxi taxi= taxiOptional.get();
		return reactiveRedisTemplate.opsForGeo().add(taxi.getTaxiType().toString(),
				locationToPointConverter.convert(locationDTO), taxiId.toString())
				.flatMap(l->Mono.just(taxi));

	}
	
	public Flux<GeoResult<GeoLocation<String>>> getAvailableTaxis(TaxiType taxiType, Double latitude, Double longitude, Double radius) {
		return reactiveRedisTemplate.opsForGeo().radius(taxiType.toString(), 
				new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS)));
	}
	
	public Mono<TaxiStatus> getTaxiStatus(String taxiId){
		Optional<Taxi> taxiOptional= taxiRepository.findById(taxiId);
		if(!taxiOptional.isPresent()) {
			throw getTaxiIdNotFoundException(taxiId);
		}
		
		Taxi taxi= taxiOptional.get();
		
		return Mono.just(taxi.getTaxiStatus());
	}
	
	public Mono<Taxi> updateTaxiStatus(String taxiId, TaxiStatus taxiStatus){
		Optional<Taxi> taxiOptional= taxiRepository.findById(taxiId);
		if(!taxiOptional.isPresent()) {
			throw getTaxiIdNotFoundException(taxiId);
		}
		
		Taxi taxi= taxiOptional.get();
		taxi.setTaxiStatus(taxiStatus);
		
		return Mono.just(taxiRepository.save(taxi));
	}

	private TaxiIdNotFoundException getTaxiIdNotFoundException(String taxiId) {
		return new TaxiIdNotFoundException("Taxi Id " + taxiId + " Not found.");
	}
}
