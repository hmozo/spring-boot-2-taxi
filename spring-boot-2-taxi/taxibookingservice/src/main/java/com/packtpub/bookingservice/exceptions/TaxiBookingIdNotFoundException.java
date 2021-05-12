package com.packtpub.bookingservice.exceptions;

public class TaxiBookingIdNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaxiBookingIdNotFoundException(String message) {
		super(message);
	}
	
	public TaxiBookingIdNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
