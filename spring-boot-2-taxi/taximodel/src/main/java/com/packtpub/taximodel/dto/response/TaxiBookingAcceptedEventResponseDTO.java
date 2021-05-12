package com.packtpub.taximodel.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookingAcceptedEventResponseDTO {
	private String taxiBookingId;
	private String taxiId;
	private Date acceptedTime;
}
