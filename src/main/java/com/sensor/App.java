package com.sensor;

import java.io.Console;
import java.time.Duration;
import java.util.HashMap;
import java.util.Scanner;
import com.sensor.actors.BuildingManager;
import com.sensor.actors.BuildingManager.QueryAllBuildings;
import com.sensor.streams.DataStream;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.typed.ActorSystem;

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
     */    public static void main(String[] args) {
        initializeApp();
        final ActorSystem<BuildingManager.Command> buildingSys = ActorSystem
                .create(BuildingManager.create(buildingFloors, zoneCount), "building-sys");
        ActorRef stream = DataStream.getStream().run(buildingSys);         
        QueryAllBuildings qab = new QueryAllBuildings(stream);       
        Runnable r = ()->buildingSys.tell(qab);                      
        Cancellable cancellable = buildingSys.scheduler().scheduleWithFixedDelay(Duration.ofSeconds(1), Duration.ofSeconds(1), r, buildingSys.executionContext());      
        commandLoop(buildingSys, cancellable);
    }

    /**
     * Command loop for application. Will terminate our application when user enters
     * q or quit.
     * Will allow user to check for the status of each building, floor or zone using
     * s buildingName or
     * status buildingName.
     */
    private static void commandLoop(ActorSystem<BuildingManager.Command> sys, Cancellable cancelQuery) {
        System.out.println("System will now generate alerts for sensor readings. In addition,");
        System.out.println("You can enter 'quit' to terminate the program.");
        Scanner command = new Scanner(System.in);
        System.out.println("Enter command: ");
        boolean running = true;
        while (running) {
            DataStream.getStream().run(sys);           
            if (command.hasNextLine()) {
                switch (command.nextLine()) {
                    case "quit":
                        cancelQuery.cancel();
                        sys.terminate();
                        System.out.println("System terminated.");
                        running = false;
                    default:
                        System.out.println("Command not recognized!");
                        break;
                }   
            }       
        }
        command.close();
    }

    /**
     * Helper function to initialize our static final variables for the application.
     * Initialized using user input variables for the number of buildings, number of
     * floors and number of zones
     * within each floor.
     * Prints to console letting the user know that the system has been initialized
     * with entered variables.
     **/
    private static void initializeApp() {
        System.out.println("Starting Building Sensor System.");
        Console console = System.console();
        int numBuildings = Integer.valueOf(console.readLine("Enter the number of buildings to be monitored:"));
        HashMap<String, Integer> buildingMap = new HashMap<String, Integer>();
        for (int i = 1; i <= numBuildings; i++) {
            String bName = console.readLine("Enter the name of building " + i + ":");
            while (buildingMap.containsKey(bName)) {
                System.out.println("Building names must be unique.");
                bName = console.readLine("Please re-enter the name of building " + i + ":");
            }
            buildingMap.put(bName,
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
        // Initialize our static variables for functions based on the user entered
        // parameters.
        buildingFloors = buildingMap;
        zoneCount = numZones;
    }

}
