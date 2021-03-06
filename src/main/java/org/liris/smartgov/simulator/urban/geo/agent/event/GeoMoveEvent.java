package org.liris.smartgov.simulator.urban.geo.agent.event;

import org.liris.smartgov.simulator.core.events.Event;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

public class GeoMoveEvent extends Event {
	
	private LatLon oldCoordinate;
	private LatLon newCoordinate;
	private double distanceCrossed;

	public GeoMoveEvent(
			LatLon oldCoordinate,
			LatLon newCoordinate,
			double distanceCrossed) {
		this.oldCoordinate = oldCoordinate;
		this.newCoordinate = newCoordinate;
		this.distanceCrossed = distanceCrossed;
	}

	public LatLon getOldCoordinate() {
		return oldCoordinate;
	}

	public LatLon getNewCoordinate() {
		return newCoordinate;
	}
	
	public double getDistanceCrossed() {
		return distanceCrossed;
	}
	
	

}
