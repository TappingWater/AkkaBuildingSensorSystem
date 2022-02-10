package actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import utility.DeviceInfo;
import utility.ReadingConfig;

public class LightSensor extends AbstractBehavior<LightSensor.Command> {
    public interface Command{}

    public static final class GenerateLight implements Command {
        akka.actor.ActorRef stream;

        public GenerateLight(akka.actor.ActorRef stream2) {
            this.stream = stream2;
        }
    }

    public static Behavior<Command> create(String buildingName, int floor, int zone) {
        return Behaviors.setup(context -> new LightSensor(context, buildingName, floor, zone));
    }

    private final String building;
    private final int floor;
    private final int zone;

    private LightSensor(ActorContext<Command> context, String buildingName, int floor, int zone) {
        super(context);
        this.building = buildingName;
        this.floor = floor;
        this.zone = zone;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GenerateLight.class, this::generateReading)
                .onSignal(PostStop.class, signal ->terminate())
                .build();
    }

    private Behavior<Command> generateReading(GenerateLight r) {
        double randVal = (double) ((Math.random() * ((ReadingConfig.lightUpper+1) - (ReadingConfig.lightLower-1) + (ReadingConfig.lightLower + 1))));
        DeviceInfo dataReading = new DeviceInfo(building, floor, zone, randVal, DeviceInfo.types.LIGHT);
        r.stream.tell(dataReading, null);
        return this;
    }

    private LightSensor terminate() {
        System.out.printf("Light sensor in zone %d of floor %d inside building %s terminated \n", zone, floor, building);
        return this;
    }
}
