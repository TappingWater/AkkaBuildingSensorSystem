package com.sensor.actors;

import java.util.HashMap;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.stream.javadsl.RunnableGraph;

/**
 * This class represents the top level element in the user actor hierachy.
 */
public class BuildingManager extends AbstractBehavior<BuildingManager.Command> {

	public interface Command {
	}

	// HashMap to maintain actor references to all the buildings that this actor manages.
	private final static HashMap<String, ActorRef<Building.Command>> buildingDetails= new HashMap<String, ActorRef<Building.Command>>();

	// When we create our system we call the create behavior to create our system.
	public static Behavior<Command> create(HashMap<String, Integer> buildingSizes, int zoneCount, RunnableGraph<ActorRef> tempSource) {		
		ActorRef centralTemp = tempSource.run();
		return Behaviors.setup(context -> new BuildingManager(context, buildingSizes, zoneCount));
	}

	/**
	 * Within this constructor we create the building actors that will belong to
	 * this system and save
	 * the actor ref tied to its name if we need to query a specific group.
	 * 
	 * @param context
	 * @param buildingSizes
	 *                      Hashmap containing building names to be initialized and
	 *                      their respective floor counts.
	 * @param zoneCount
	 *                      The total amt of zones per floor which is standardized
	 *                      for the system.
	 */
	private BuildingManager(ActorContext<Command> context, HashMap<String, Integer> buildingSizes, int zoneCount) {
		super(context);
		for (String name : buildingSizes.keySet()) {
			ActorRef<Building.Command> buildingRef = getContext().spawn(Building.create(name, buildingSizes.get(name), zoneCount), name);
			buildingDetails.put(name, buildingRef);
		}
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder().onSignal(PostStop.class, signal -> terminate()).build();
	}

	private BuildingManager terminate() {
		getContext().getLog().info("Building sensor monitoring system succesfully terminated.");
		return this;
	}

}
