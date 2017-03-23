package net.pbdavey.util;

public class Arrays {
	public static double [] copyOf(double [] source, int length) {
		double [] target = new double[length];
		for (int i = 0; i < target.length; i++) {
			if (i > source.length-1)
				target[i] = 0;
			else
				target[i] = source[i];
		}
		return target;
	}

	public static float [] copyOf(float [] source, int length) {
		float [] target = new float[length];
		for (int i = 0; i < target.length; i++) {
			if (i > source.length-1)
				target[i] = 0f;
			else
				target[i] = source[i];
		}
		return target;
	}

	public static byte [] copyOf(byte [] source, int length) {
		byte [] target = new byte[length];
		for (int i = 0; i < target.length; i++) {
			if (i > source.length-1)
				target[i] = 0;
			else
				target[i] = source[i];
		}
		return target;
	}

	public static int[] copyOf(int[] source, int length) {
		int [] target = new int[length];
		for (int i = 0; i < target.length; i++) {
			if (i > source.length-1)
				target[i] = 0;
			else
				target[i] = source[i];
		}
		return target;
	}
}