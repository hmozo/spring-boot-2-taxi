package com.packtpub.taximodel.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.geo.Point;

import com.packtpub.taximodel.dto.request.LocationDTO;

public class LocationToPointConverter implements Converter<LocationDTO, Point>{

	@Override
	public Point convert(LocationDTO locationDTO) {
		if (locationDTO==null) {
			return null;
		}
		return new Point(locationDTO.getLongitude(), locationDTO.getLatitude());
	}

}
