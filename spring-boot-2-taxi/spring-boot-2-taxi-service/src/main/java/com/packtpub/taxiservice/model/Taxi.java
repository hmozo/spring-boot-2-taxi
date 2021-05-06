package com.packtpub.taxiservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.packtpub.taximodel.enums.TaxiStatus;
import com.packtpub.taximodel.enums.TaxiType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("Taxi")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Taxi {
	@Id
	private String taxiId;
	
	private TaxiType taxiType;
	
	private TaxiStatus taxiStatus;
}
