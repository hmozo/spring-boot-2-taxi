package com.packtpub.taximodel.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookingAcceptedEventDTO {

	private String taxiBookingId;
	private String taxiId;
	private Date acceptedTime= new Date();
}
