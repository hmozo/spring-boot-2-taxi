package com.packtpub.bookingservice.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("TaxiBooking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBooking<TaxiBookingStatus> {
	@Id
	private String taxiBookingId;
	private Point start;
	private Date startTime;
	private Point end;
	private Date endTime;
	private Date bookedTime;
	private Date acceptedTime;
	private Long customerId;
	private TaxiBookingStatus bookingStatus;
	private String reasonToCancel;
	private Date cancelTime;
	private String taxiId;
}
