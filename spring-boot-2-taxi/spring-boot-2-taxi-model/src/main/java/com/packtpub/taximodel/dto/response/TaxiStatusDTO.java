package com.packtpub.taximodel.dto.response;

import com.packtpub.taximodel.enums.TaxiStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiStatusDTO {
	private String taxiId;
	private TaxiStatus status;
}
