package com.sensor;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class.
 * Terminal UI code.
 */
public final class App {

    // These variables are initialized once during the lifespan of the application   
    private static HashMap<String, Integer> buildingFloors;
    private static int zoneCount;

    /**
     * Takes user input and generates iot application based on number of buildings,
     * number of floors in each building and number of zones in each floor.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        initializeApp();        
        
    }
    
    /**
    * Helper function to initialize our static final variables for the application.
    * Initialized using user input variables for the number of buildings, number of floors and number of zones
    * within each floor.
    * Prints to console letting the user know that the system has been initialized with entered variables.
    **/
    private static void initializeApp() {
        System.out.println("Starting Building Sensor System.");
        Console console = System.console();
        int numBuildings = Integer.valueOf(console.readLine("Enter the number of buildings to be monitored:"));
        HashMap<String, Integer> buildingMap = new HashMap<String, Integer>();
        for (int i = 1; i <= numBuildings; i++) {
            buildingMap.put(console.readLine("Enter the name of building " + i + ":"),
                    Integer.valueOf(console.readLine("Enter the number of floors for floor " + i + ":")));
        }
        System.out.println("A zone is a dedicated area within a floor with its' own group of sensors.");
        int numZones = Integer.valueOf(console.readLine("Enter the number of zones in each floor:"));
        System.out.println();
        System.out.println("Initiating system for " + numBuildings + " buildings");
        for (String building : buildingMap.keySet()) {
            System.out.println("Monitoring building: " + building + " with " + buildingMap.get(building) + " floors");
        }
        System.out.println("Each floor has " + numZones + " zones");
        buildingFloors = buildingMap;
        zoneCount = numZones;
    }

}
