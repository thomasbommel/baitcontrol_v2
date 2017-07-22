package baitcontrol.v2;

import java.util.Date;

public class GPVTGEvent {
	private  Double speed;
	private  Date time;
	
	public GPVTGEvent(Double speed,Date time) {
		this.speed = speed;
		this.time = time;
	}

	public Double getSpeed() {
		return speed;
	}

	public Date getTime() {
		return time;
	}
}
