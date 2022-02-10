package actors;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.HashMap;

/**
 * This class represents a single building in our application.
 * This actor will manage floors and will be the second top most
 * user level actor.
 */
public class Building extends AbstractBehavior<Building.Command> {

    public interface Command {
    }

    private final String name;
    private final HashMap<Integer, ActorRef<Floor.Command>> floorDetails = new HashMap<Integer, ActorRef<Floor.Command>>();

    // When we create our system we call the create behavior to create our system.
    public static Behavior<Command> create(String buildingName, int floorCount, int zoneCount) {
        return Behaviors.setup(context -> new Building(context, buildingName, floorCount, zoneCount));
    }

    public static final class QueryAllFloors implements Command {
        akka.actor.ActorRef stream;

        public QueryAllFloors(akka.actor.ActorRef stream2) {
            this.stream = stream2;
        }
    }

    private Building(ActorContext<Command> context, String buildingName, int floorCount, int zoneCount) {
        super(context);
        this.name = buildingName;
        for (int i = 1; i <= floorCount; i++) {
            floorDetails.put(i, getContext().spawn(Floor.create(name, i, zoneCount), String.valueOf(i)));
        }
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(QueryAllFloors.class, this::queryAllFloors)
                .onSignal(PostStop.class, signal -> terminate())
                .build();
    };

    private Behavior<Building.Command> queryAllFloors(QueryAllFloors q) {
        for (Integer floor: floorDetails.keySet()) {
            ActorRef<Floor.Command> floorRef = floorDetails.get(floor);
            Floor.QueryAllZones qaz = new Floor.QueryAllZones(q.stream);
            floorRef.tell(qaz);
        }
        return this;
    }

    /**
     * Terminates the application
     * @return
     */
    private Building terminate() {
        System.out.printf("Building %s within system terminated. \n", name);
        return this;
    }

}
