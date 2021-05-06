package com.packtpub.taximodel.dto.request;

import java.util.Date;
import java.util.UUID;

import com.packtpub.taximodel.enums.TaxiType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookedEventDTO {
	private String taxiBookingId= UUID.randomUUID().toString();
	
	private LocationDTO start;

    private LocationDTO end;

    private Date bookedTime = new Date();

    private Long customerId;

    private TaxiType taxiType;
}
