package com.sensor.utility;

/**
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
	private String type;

	public DeviceInfo(String building, int floorNum, int zoneNum, Double reading, String type) {
		this.buildingName = building;
		this.floorNumber = floorNum;
		this.zoneNumber = zoneNum;
		this.reading = reading;
		this.type = type;
	}

	public void setType(String type) {
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

	public String getType() {
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

	@Override
	public String toString() {
		return "Temperature reading of " + reading + " detected on zone " + zoneNumber + " , located on floor "
				+ floorNumber + " inside building named" + buildingName;
	}

}
