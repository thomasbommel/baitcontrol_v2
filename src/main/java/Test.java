

public class Test {
	
	private static final double SECONDS_TO_DROP = 0.1; // in seconds
	private static final double MAXIMUM_TIME_TO_DROP_IN_SECONDS = 10;

	private static final long DISTANCE_BETWEEN_DROPS = 50; // in meter
	
	public static void main(String[] args) {
		testDistancePerSecond();
	}
	
	public static void testDelayPerSpeed(){	
		for(double i = 0 ;i<=100;i++){
			System.out.println(Utils.numberToString(i, 4, 0)+" "+Utils.numberToString((double)DropController.getInstance().getDelayForKMH(i)/1000.0, 14, 4));
		}
	}
	
	
	public static void testDistancePerSecond(){	
		for(double i = 0 ;i<=200;i++){
			System.out.println(Utils.numberToString(i, 4, 0)+"  1 u/s ... "+Utils.numberToString((double) i/3.6, 5, 2)+"m   5 u/s ... "+Utils.numberToString((double) i/3.6/5, 5, 2)+"m   10 u/s ... "+Utils.numberToString((double) i/3.6/10, 5, 2)+"m");
		}
	}
}
