package com.sensor.actors;

import java.util.HashMap;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * This class represents a single building in our application.
 * This actor will manage floors and will be the second top most
 * user level actor.
 */
public class Building extends AbstractBehavior<Building.Command> {

	public interface Command {
	}

	private final String name;
	private final int floorCount;
	private final int zoneCount;
	private final HashMap<Integer, ActorRef<Floor.Command>> floorDetails = new HashMap<Integer, ActorRef<Floor.Command>>();

	// When we create our system we call the create behavior to create our system.
	public static Behavior<Command> create(String buildingName, int floorCount, int zoneCount) {
		return Behaviors.setup(context -> new Building(context, buildingName, floorCount, zoneCount));
	}

	private Building(ActorContext<Command> context, String buildingName, int floorCount, int zoneCount) {
		super(context);
		this.name = buildingName;
		this.floorCount = floorCount;
		this.zoneCount = zoneCount;
		for (int i = 1; i <= floorCount; i++) {
			floorDetails.put(i, getContext().spawn(Floor.create(i, zoneCount), String.valueOf(i)));
		}
	}

	@Override
	public Receive<Command> createReceive() {
		return null;
	};

}
