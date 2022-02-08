package com.sensor.utility;

/**
 * Config class to set the upper and lower limits of our application.
 * If our readings go above or below these ratings, we will create produce
 * a notification.
 */
public class ReadingConfig {
	
	public static final Double tempUpper = 75.00;
	public static final Double tempLower = 65.00;

	
}
