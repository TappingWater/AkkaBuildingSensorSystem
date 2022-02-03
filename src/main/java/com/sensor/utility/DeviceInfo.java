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

	public DeviceInfo() {		
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

	public String getBuildingName(){
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

}
