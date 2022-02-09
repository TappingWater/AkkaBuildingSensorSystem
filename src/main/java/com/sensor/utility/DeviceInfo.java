package com.sensor.utility;

/**
 * Utility class used to feed information into a stream from the sensor.
 * Contains the needed information for a particular device such as:
 * building name
 * floor number
 * zone number
 */
public class DeviceInfo {

	private String buildingName;
	private int floorNumber;
	private int zoneNumber;
	private Double reading;
	private types type;

	public static enum types {
		TEMP, LIGHT, HUMIDITY
	}

	public DeviceInfo(String building, int floorNum, int zoneNum, Double reading, types type) {
		this.buildingName = building;
		this.floorNumber = floorNum;
		this.zoneNumber = zoneNum;
		this.reading = reading;
		this.type = type;
	}

	public void setType(types type) {
		this.type = type;
	}

	public void setBuildingName(String n) {
		this.buildingName = n;
	}

	public void setFloorNumber(int n) {
		this.floorNumber = n;
	}

	public void setZoneNumber(int n) {
		this.zoneNumber = n;
	}

	public void setReading(Double d) {
		reading = d;
	}

	public types getType() {
		return type;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public int zoneNumber() {
		return zoneNumber;
	}

	public Double getReading() {
		return reading;
	}

	/**
	 * Override the print method so that we can print device info objects directly
	 * through system.out.println within the stream. Any changes to the format of the stream must be done here.
	 */
	@Override
	public String toString() {
		if (type == types.TEMP) {
			return "Temperature reading of " + reading + " detected on zone " + zoneNumber + " , located on floor "
				+ floorNumber + " ,inside building named " + buildingName;
		} else if (type == types.HUMIDITY) {
			return "Humidity reading of " + reading + " detected on zone " + zoneNumber + " , located on floor "
				+ floorNumber + " ,inside building named " + buildingName;
		} else {
			return "Light reading of " + reading + " detected on zone " + zoneNumber + " , located on floor "
				+ floorNumber + " ,inside building named " + buildingName;
		}
		
	}

}
