package com.packtpub.taximodel.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookingCancelledEventDTO {
	private String taxiBookingId;
	private String reason;
	private Date cancelTime= new Date();
}
