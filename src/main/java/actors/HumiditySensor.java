package actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import utility.DeviceInfo;
import utility.ReadingConfig;

public class HumiditySensor extends AbstractBehavior<HumiditySensor.Command> {

    public interface Command{}

    public static final class GenerateHumidity implements Command {
        akka.actor.ActorRef stream;

        public GenerateHumidity(akka.actor.ActorRef stream2) {
            this.stream = stream2;
        }
    }

    public static Behavior<Command> create(String buildingName, int floor, int zone) {
        return Behaviors.setup(context -> new HumiditySensor(context, buildingName, floor, zone));
    }

    private final String building;
    private final int floor;
    private final int zone;

    private HumiditySensor(ActorContext<Command> context, String buildingName, int floor, int zone) {
        super(context);
        this.building = buildingName;
        this.floor = floor;
        this.zone = zone;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GenerateHumidity.class, this::generateReading)
                .onSignal(PostStop.class, signal ->terminate())
                .build();
    }

    private Behavior<Command> generateReading(GenerateHumidity r) {
        double randVal = (double) ((Math.random() * ((ReadingConfig.humidityUpper+3) - (ReadingConfig.humidityLower-3) + (ReadingConfig.humidityLower-3))));
        DeviceInfo dataReading = new DeviceInfo(building, floor, zone, randVal, DeviceInfo.types.HUMIDITY);
        r.stream.tell(dataReading, null);
        return this;
    }

    private HumiditySensor terminate() {
        System.out.printf("Humidity sensor in zone %d of floor %d inside building %s terminated", zone, floor, building);
        return this;
    }
}
