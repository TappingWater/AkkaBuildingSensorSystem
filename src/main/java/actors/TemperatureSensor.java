package actors;


import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import utility.DeviceInfo;

/**
 * Actor object that represents a temperature sensor.
 */
public class TemperatureSensor extends AbstractBehavior<TemperatureSensor.Command> {

    // Nested marker interface to represent the types of messages that the sensor
    // can process.
    public interface Command {}

    /**
     * Message to be passed to this actor to produce a behavior that will
     * pass the produced reading from the sensor to the stream.
     */
    public static final class GenerateTemp implements Command {
        akka.actor.ActorRef stream;

        public GenerateTemp(akka.actor.ActorRef stream2) {
            this.stream = stream2;
        }
    }

    // To create a new sensor, we need to consider certain factors such as building,
    // floor and zone
    // Recommended to create new devices using setup() method.
    public static Behavior<Command> create(String buildingName, int floor, int zone) {
        return Behaviors.setup(context -> new TemperatureSensor(context, buildingName, floor, zone));
    }

    private final String building;
    private final int floor;
    private final int zone;

    // Constructor defined as private. New sensor actors created using create.
    private TemperatureSensor(ActorContext<Command> context, String buildingName, int floor, int zone) {
        super(context);
        this.building = buildingName;
        this.floor = floor;
        this.zone = zone;
    }

    /**
     * Default method for an akka actor that needs to be overriden.
     * We create a new receive builder object where we can match the message types
     * our actor receives and how we process them and change the state of actor if
     * needed.
     */
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GenerateTemp.class, this::generateReading)
                .onSignal(PostStop.class, signal ->terminate())
                .build();
    }

    /**
     * Method that pushes a randomized reading from the sensor
     * onto our stream when requested by the scheduler.
     * @param r
     * @return
     */
    private Behavior<Command> generateReading(GenerateTemp r) {
        double randVal = (double) ((Math.random() * (76 - 64) + 64));
        DeviceInfo dataReading = new DeviceInfo(building, floor, zone, randVal, DeviceInfo.types.TEMP);
        r.stream.tell(dataReading, null);
        return this;
    }

    /**
     * Terminates the application
     * @return
     */
    private TemperatureSensor terminate() {
        System.out.printf("Temp sensor in zone %d of floor %d inside building %s terminated \n", zone, floor, building);
        return this;
    }
}
