package com.packtpub.taxiservice.listeners;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packtpub.taximodel.dto.request.TaxiBookingAcceptedEventDTO;
import com.packtpub.taximodel.enums.TaxiStatus;
import com.packtpub.taxiservice.services.TaxiService;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;

@Component
public class TaxiBookingAcceptedEventMessageListener implements MessageListener{
	
	private static final Logger LOGGER= LoggerFactory.getLogger(TaxiBookingAcceptedEventMessageListener.class);
	
	private final TaxiService taxiService;
	private final ObjectMapper objectMapper= new ObjectMapper();
	
	public TaxiBookingAcceptedEventMessageListener(TaxiService taxiService) {
		this.taxiService= taxiService;
	}

	@Override
	public void onMessage(Message message, @Nullable byte[] pattern) {
		try {
			TaxiBookingAcceptedEventDTO taxiBookingAcceptedEventDTO= objectMapper.readValue(new String(message.getBody()), TaxiBookingAcceptedEventDTO.class);
			LOGGER.info("Accepted Event {}", taxiBookingAcceptedEventDTO);
			taxiService.updateTaxiStatus(taxiBookingAcceptedEventDTO.getTaxiId(), TaxiStatus.OCCUPIED);
		}catch(IOException e) {
			LOGGER.error("Error while updating taxi status", e);
		}	
	}
}
