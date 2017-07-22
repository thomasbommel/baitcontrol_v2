package baitcontrol.v2;

import java.util.Date;
import java.util.logging.Logger;

import exceptions.TooLowSpeedException;
import utils.Utils;

public class DropEvent {
	private static final Logger LOGGER = Logger.getLogger(DropEvent.class.getName());
	private Double lng;
	private Double lat;
	private Date time;
	private Double speed;

	public DropEvent(Double lat, Double lng, Double speed, Date time) {
		this.lng = lng;
		this.lat = lat;
		this.time = time;
		this.speed = speed;
	}

	public Double getLng() {
		return lng;
	}

	public Double getLat() {
		return lat;
	}

	public Double getSpeed() {
		return speed;
	}

	public Date getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		try {
			return "DropEvent: [time: " + Utils.dateToString(time) + ", lat: " + Utils.numberToString(lat) + ", lng: " + Utils.numberToString(lng) + ", speed: "
					+ Utils.numberToString(speed,8,2)+", delay: "+Utils.numberToString(DropController.getInstance().getDelay(speed),10,0)+"]";
		} catch (TooLowSpeedException e) { // should never occur
			LOGGER.severe("there was an invalid drop DropEvent: [time: " + Utils.dateToString(time) + ", lat: " + Utils.numberToString(lat) + ", lng: " + Utils.numberToString(lng) + ", speed: "
					+ Utils.numberToString(speed,8,2)+"]");
			return "invalid drop "+e.getMessage();
		}
	}
}
