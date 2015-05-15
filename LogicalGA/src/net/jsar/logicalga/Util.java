package net.jsar.logicalga;

public class Util {

	public static boolean[] double2boolean(double[] d) {
		boolean out[] = new boolean[d.length];
		for (int i=0; i<d.length; i++) {
			out[i] = (d[i] > 0) ? true : false;
		}
		return out;
	}
	
	public static double[] boolean2double(boolean[] d) {
		double out[] = new double[d.length];
		for (int i=0; i<d.length; i++) {
			out[i] = (d[i]) ? 1 : 0;
		}
		return out;
	}
	
	public static boolean double2boolean(double d) {
		return (d > 0) ? true : false;
	}
	
	public static double boolean2double(boolean d) {
		return (d) ? 1 : 0;
	}
}
