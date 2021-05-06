package com.packtpub.taximodel.dto.request;

import java.util.UUID;

import com.packtpub.taximodel.enums.TaxiType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiRegisterEventDTO {
	private String taxiID= UUID.randomUUID().toString();
	private TaxiType taxiType;
}
