package baitcontrol.v2;

import java.util.Date;

public class GPGGAEvent {
	private  Double lng;
	private  Double lat;
	private  Date time;
	
	public GPGGAEvent(Double lat, Double lng,Date time) {
		this.lng = lng;
		this.lat = lat;
		this.time = time;
	}

	public Double getLng() {
		return lng;
	}

	public Double getLat() {
		return lat;
	}

	public Date getTime() {
		return time;
	}
	
	
}
