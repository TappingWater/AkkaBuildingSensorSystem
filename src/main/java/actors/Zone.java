package actors;

import akka.actor.typed.PostStop;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * Actor class  that represents a zone in a building.
 * A zone will have a dedicated group of sensors for temperature,
 * humidity and illumination.
 */
public class Zone extends AbstractBehavior<Zone.Command> {
    public interface Command {}

    private final String buildingName;
    private final int floorNum;
    private final int zoneNum;
    private ActorRef<TemperatureSensor.Command> tempSensor;
    private ActorRef<HumiditySensor.Command> humiditySensor;
    private ActorRef<LightSensor.Command> lightSensor;

    public static final class QueryAllSensors implements Command {
        akka.actor.ActorRef stream;

        public QueryAllSensors(akka.actor.ActorRef stream2) {
            this.stream = stream2;
        }
    }

    private Zone(ActorContext<Command> context, int zoneNo, String buildingName, int floorNum) {
        super(context);
        this.zoneNum = zoneNo;
        this.buildingName = buildingName;
        this.floorNum = floorNum;
        tempSensor = context.spawn(TemperatureSensor.create(buildingName, floorNum, zoneNum), "temp-sensor");
        lightSensor = context.spawn(LightSensor.create(buildingName, floorNum, zoneNum), "light-sensor");
        humiditySensor = context.spawn(HumiditySensor.create(buildingName, floorNum, zoneNum), "humidity-sensor");
    }

    // When we create our system we call the create behavior to create our system.
    public static Behavior<Command> create(int zoneNum, String buildingName, int floorNum) {
        return Behaviors.setup(context -> new Zone(context, zoneNum, buildingName, floorNum));
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(QueryAllSensors.class, this::querySensors)
                .onSignal(PostStop.class, signal->terminate())
                .build();
    }

    //Method that queries all sensors in zone to generate a reading.
    private Behavior<Command> querySensors(QueryAllSensors q) {
        TemperatureSensor.GenerateTemp gt = new TemperatureSensor.GenerateTemp(q.stream);
        HumiditySensor.GenerateHumidity gh = new HumiditySensor.GenerateHumidity(q.stream);
        LightSensor.GenerateLight gl = new LightSensor.GenerateLight(q.stream);
        tempSensor.tell(gt);
        humiditySensor.tell(gh);
        lightSensor.tell(gl);
        return this;
    }

    /**
     * Terminates the application
     * @return
     */
    private Zone terminate() {
       System.out.printf("Zone %d actor for floor %d in building %s has been succesfully terminated.\n", zoneNum, floorNum, buildingName);
        return this;
    }

}
