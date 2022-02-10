import actors.BuildingManager;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.typed.ActorSystem;
import streams.DataStream;

import java.time.Duration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class App {
    // These variables are initialized once during the lifespan of the application
    private static HashMap<String, Integer> buildingFloors;
    private static int zoneCount;
    private static int numBuildings;

    /**
     * Takes user input and generates iot application based on number of buildings,
     * number of floors in each building and number of zones in each floor.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        initializeApp();
        final ActorSystem<BuildingManager.Command> buildingSys = ActorSystem
                .create(BuildingManager.create(buildingFloors, zoneCount), "building-sys");
        ActorRef stream = DataStream.getStream().run(buildingSys);
        BuildingManager.QueryAllBuildings qab = new BuildingManager.QueryAllBuildings(stream);
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
            if (command.hasNextLine()) {
                if (command.nextLine().equals("quit")) {
                    System.out.println("listening");
                    cancelQuery.cancel();
                    sys.terminate();
                    running = false;
                } else {
                    System.out.println("Command not recognized");
                }
            }
        }
        System.out.println("System terminated.");
        command.close();
    }

    private static Pattern pattern = Pattern.compile("\\d+");

    public static boolean validInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    /**
     * Helper function to initialize our static final variables for the application.
     * Initialized using user input variables for the number of buildings, number of
     * floors and number of zones within each floor.
     * Prints to console letting the user know that the system has been initialized
     * with entered variables and checks for valid input.
     **/
    private static void initializeApp() {
        System.out.println("Starting Building Sensor System.");
        Scanner console = new Scanner(System.in);
        // Initiate building system
        System.out.print("Enter the number of buildings for this application: ");
        String s = console.nextLine();
        while (!validInteger(s)) {
            System.out.print("Please enter a valid integer for the number of buildings: ");
            s = console.nextLine();
        }
        numBuildings = Integer.parseInt(s);
        System.out.println("Initiating building system with "+numBuildings +" buildings.");
        HashMap<String, Integer> buildingMap = new HashMap<String, Integer>();
        // Initialize buildings maps with respective number of floors
        for (int i = 1; i <= numBuildings; i++) {
            System.out.print("Enter the name of building " + i + ":");
            String bName = console.nextLine();
            while (buildingMap.containsKey(bName)) {
                System.out.print("Building names must be unique.Please re-enter the name of building " + i);
                bName = console.nextLine();
            }
            System.out.print("Enter the number of floors for building "+bName+":");
            String floors = console.nextLine();
            while (!validInteger(floors)) {
                System.out.print("Please enter a valid integer for the number of buildings: ");
                floors = console.nextLine();
            }
            buildingMap.put(bName, Integer.parseInt(floors));
        }
        buildingFloors = buildingMap;
        //Initialize zones for building
        System.out.println("A zone is a dedicated area within a floor with its' own group of sensors.");
        System.out.print("Enter the number of zones for the system: ");
        String zones = console.nextLine();
        while (!validInteger(zones)) {
            System.out.print("Please enter a valid integer for number of zones: ");
            zones = console.nextLine();
        }
        zoneCount = Integer.parseInt(zones);
        System.out.println("Initiating system with "+zoneCount+" zones");
    }
}
