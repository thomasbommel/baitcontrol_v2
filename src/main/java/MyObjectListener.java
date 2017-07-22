

import de.taimos.gpsd4java.api.IObjectListener;
import de.taimos.gpsd4java.types.ATTObject;
import de.taimos.gpsd4java.types.DeviceObject;
import de.taimos.gpsd4java.types.DevicesObject;
import de.taimos.gpsd4java.types.SKYObject;
import de.taimos.gpsd4java.types.subframes.SUBFRAMEObject;

public abstract class MyObjectListener implements IObjectListener{

	public void handleSKY(SKYObject sky) {
		//do nothing
	}

	public void handleATT(ATTObject att) {
		//do nothing
	}

	public void handleSUBFRAME(SUBFRAMEObject subframe) {
		//do nothing
	}

	public void handleDevices(DevicesObject devices) {
		//do nothing
	}

	public void handleDevice(DeviceObject device) {
		//do nothing
	}

}
