package com.sensor.actors;

import java.util.HashMap;

import akka.actor.typed.PostStop;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * Actor that represents a single floor within a building.
 */
public class Floor extends AbstractBehavior<Floor.Command> {

	public interface Command {}

	private final String buildingName;
	private final int floorNum;

	private final HashMap<Integer, ActorRef<Zone.Command>> zoneDetails = new HashMap<Integer, ActorRef<Zone.Command>>();

	//Message to query all zones within a floor.
	public static enum QueryAllZones implements Command {
		INSTANCE
	}

	// When we create our system we call the create behavior to create our system.
	public static Behavior<Command> create(String buildingName, int floorNum, int zoneCount) {
		return Behaviors.setup(context -> new Floor(context, buildingName, floorNum, zoneCount));
	}

	/**
	 * Constructor to generate a floor actor with floor and zone counts
	 * @param context
	 * @param floorNum
	 * The respective floor within the building for this actor. ex: floor 1
	 * @param zoneCount
	 * The number of zones per floor
	 */
	private Floor(ActorContext<Command> context, String buildingName, int floorNum, int zoneCount) {
		super(context);	
		this.buildingName = buildingName;
		this.floorNum = floorNum;	
		for (int i = 1; i <= zoneCount; i++) {
			zoneDetails.put(i, context.spawn(Zone.create(i, buildingName, floorNum), String.valueOf(i)));
		}
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
			.onMessageEquals(QueryAllZones.INSTANCE, this::queryAllZones)
			.onSignal(PostStop.class, signal -> terminate())
			.build();
	};
	
	private Behavior<Floor.Command> queryAllZones() {
		for (Integer zone: zoneDetails.keySet()) {
			ActorRef<Zone.Command> zoneRef = zoneDetails.get(zone);
			zoneRef.tell(Zone.QueryAllSensors.INSTANCE);
		}
		return this;
	}

	/**
	 * Terminates the application
	 * @return
	 */
	private Floor terminate() {
		getContext().getLog().info("Floor actor %d for building %s terminated.", floorNum, buildingName);
		return this;
	}

}
