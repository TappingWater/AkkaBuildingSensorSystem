package com.sensor.actors;

import java.util.HashMap;

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

	public interface Command {
	}

	private final int floorNum;
	private final int zoneCount;
	private final HashMap<Integer, ActorRef<Zone.Command>> zoneDetails = new HashMap<Integer, ActorRef<Zone.Command>>();

	// When we create our system we call the create behavior to create our system.
	public static Behavior<Command> create(int floorNum, int zoneCount) {
		return Behaviors.setup(context -> new Floor(context, floorNum, zoneCount));
	}

	/**
	 * Constructor to generate a floor actor with floor and zone counts
	 * @param context
	 * @param floorNum
	 * The respective floor within the building for this actor. ex: floor 1
	 * @param zoneCount
	 * The number of zones per floor
	 */
	private Floor(ActorContext<Command> context, int floorNum, int zoneCount) {
		super(context);
		this.floorNum = floorNum;
		this.zoneCount = zoneCount;
		for (int i = 1; i <= zoneCount; i++) {
			zoneDetails.put(i, context.spawn(Zone.create(i), String.valueOf(i)));
		}
	}

	@Override
	public Receive<Command> createReceive() {
		return null;
	};

}
