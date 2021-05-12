package com.packtpub.bookingservice.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packtpub.bookingservice.exceptions.TaxiBookingIdNotFoundException;
import com.packtpub.bookingservice.model.TaxiBooking;
import com.packtpub.bookingservice.repositories.TaxiBookingRepository;
import com.packtpub.taxiconfig.config.RedisConfig;
import com.packtpub.taximodel.converter.LocationToPointConverter;
import com.packtpub.taximodel.dto.request.TaxiBookedEventDTO;
import com.packtpub.taximodel.dto.request.TaxiBookingAcceptedEventDTO;
import com.packtpub.taximodel.dto.request.TaxiBookingCancelledEventDTO;
import com.packtpub.taximodel.enums.TaxiBookingStatus;
import com.packtpub.taximodel.enums.TaxiType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TaxiBookingService {
	
	private static Logger logger= LoggerFactory.getLogger(TaxiBookingService.class);
	
	private final RedisTemplate<String, String> redisTemplate;
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final TaxiBookingRepository taxiBookingRepository;
	private final ObjectMapper objectMapper= new ObjectMapper();
	private final LocationToPointConverter locationToPointConverter= new LocationToPointConverter();

	public TaxiBookingService(RedisTemplate<String, String> redisTemplate, ReactiveRedisTemplate<String, String> reactiveRedisTemplate, 
			TaxiBookingRepository taxiBookingRepository) {
		this.redisTemplate= redisTemplate;
		this.reactiveRedisTemplate= reactiveRedisTemplate;
		this.taxiBookingRepository= taxiBookingRepository;
	}
	
	public Mono<TaxiBooking> book(TaxiBookedEventDTO taxiBookedEventDTO){
		TaxiBooking taxiBooking= new TaxiBooking();
		taxiBooking.setEnd(locationToPointConverter.convert(taxiBookedEventDTO.getEnd()));
		taxiBooking.setStart(locationToPointConverter.convert(taxiBookedEventDTO.getStart()));
		taxiBooking.setBookedTime(taxiBookedEventDTO.getBookedTime());
		taxiBooking.setCustomerId(taxiBookedEventDTO.getCustomerId());
		taxiBooking.setBookingStatus(TaxiBookingStatus.ACTIVE);
		TaxiBooking savedTaxiBooking= taxiBookingRepository.save(taxiBooking);
		
		return reactiveRedisTemplate.opsForGeo().add(getTaxiTypeBookings(taxiBookedEventDTO.getTaxiType()), 
				taxiBooking.getStart(), taxiBooking.getTaxiBookingId())
				.flatMap(l->Mono.just(savedTaxiBooking));
	}


	public Mono<TaxiBooking> cancel(String taxiBookingId, TaxiBookingCancelledEventDTO taxiBookingCancelledEventDTO){
		Optional<TaxiBooking> taxiBookingOptional= taxiBookingRepository.findById(taxiBookingId);
		if(!taxiBookingOptional.isPresent()) {
			throw getTaxiBookingIdNotFoundException(taxiBookingId);
		}
		
		TaxiBooking taxiBooking= taxiBookingOptional.get();
		taxiBooking.setBookingStatus(TaxiBookingStatus.CANCELLED);
		taxiBooking.setReasonToCancel(taxiBookingCancelledEventDTO.getReason());
		taxiBooking.setCancelTime(taxiBookingCancelledEventDTO.getCancelTime());
		return Mono.just(taxiBookingRepository.save(taxiBooking));
	}
	
	public Mono<TaxiBooking> accept(String taxiBookingId, TaxiBookingAcceptedEventDTO taxiBookingAcceptedEventDTO){
		Optional<TaxiBooking> taxiBookingOptional= taxiBookingRepository.findById(taxiBookingId);
		
		if(!taxiBookingOptional.isPresent()) {
			throw getTaxiBookingIdNotFoundException(taxiBookingId);
		}
		
		TaxiBooking taxiBooking= taxiBookingOptional.get();
		taxiBooking.setTaxiId(taxiBookingAcceptedEventDTO.getTaxiId());
		taxiBooking.setAcceptedTime(taxiBookingAcceptedEventDTO.getAcceptedTime());
		return Mono.just(taxiBookingRepository.save(taxiBooking))
				.doOnSuccess(t->{
					try {
						redisTemplate.convertAndSend(RedisConfig.ACCEPTED_EVENT_CHANNEL, 
								objectMapper.writeValueAsString(taxiBookingAcceptedEventDTO));
					}catch(JsonProcessingException e) {
						logger.error("Error while sending message to channel{}");
					}
				});
	}
	
	public Flux<GeoResult<GeoLocation<String>>> getBookings(TaxiType taxiType, Double latitude, Double longitude, Double radius){
		return reactiveRedisTemplate.opsForGeo()
				.radius(getTaxiTypeBookings(taxiType), new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS)));
	}
	
	public Mono<TaxiBooking> updateBookingStatus(String taxiBookingId, 
			TaxiBookingStatus taxiBookingStatus){
		Optional<TaxiBooking> taxiBookingOptional= taxiBookingRepository.findById(taxiBookingId);
		
		if(!taxiBookingOptional.isPresent()) {
			throw getTaxiBookingIdNotFoundException(taxiBookingId);
		}
		
		TaxiBooking taxiBooking= taxiBookingOptional.get();
		taxiBooking.setBookingStatus(taxiBookingStatus);
		return Mono.just(taxiBookingRepository.save(taxiBooking));
		
	}

	private TaxiBookingIdNotFoundException getTaxiBookingIdNotFoundException(String taxiBookingId) {
		return new TaxiBookingIdNotFoundException("Taxi booking id " + taxiBookingId + " not found.");
	}
	
	private String getTaxiTypeBookings(TaxiType taxiType) {
		return taxiType.toString() + "-Bookings";
	}

}
