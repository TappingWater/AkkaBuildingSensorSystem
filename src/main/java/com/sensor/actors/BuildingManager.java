package com.sensor.actors;

import java.util.HashMap;
import com.sensor.actors.Building.QueryAllFloors;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * This class represents the top level element in the user actor hierachy.
 */
public class BuildingManager extends AbstractBehavior<BuildingManager.Command> {

	public interface Command {
	}

	public static final class QueryAllBuildings implements Command {
		akka.actor.ActorRef stream;

		public QueryAllBuildings(akka.actor.ActorRef stream2) {
			this.stream = stream2;
		}

	}

	// HashMap to maintain actor references to all the buildings that this actor manages.
	private final static HashMap<String, ActorRef<Building.Command>> buildingDetails= new HashMap<String, ActorRef<Building.Command>>();

	// When we create our system we call the create behavior to create our system.
	public static Behavior<Command> create(HashMap<String, Integer> buildingSizes, int zoneCount) {			
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
		return newReceiveBuilder()
			.onMessage(QueryAllBuildings.class, this::queryAllBuildings)
			.onSignal(PostStop.class, signal -> terminate())
			.build();
	}

	/**
	 * Method to query all temperature sensors in the application.
	 * @return
	 */
	private Behavior<Command> queryAllBuildings(QueryAllBuildings q) {
		for (String building: buildingDetails.keySet()) {
			ActorRef<Building.Command> bRef = buildingDetails.get(building);
			QueryAllFloors qaf = new QueryAllFloors(q.stream);
			bRef.tell(qaf);
		}
		return this;
	}

	/**
	 * Terminates the application
	 * @return
	 */
	private BuildingManager terminate() {
		getContext().getLog().info("Building sensor monitoring system succesfully terminated.");
		return this;
	}

}
