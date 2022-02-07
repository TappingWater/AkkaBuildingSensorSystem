package com.sensor.actors;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class QueryActor extends AbstractBehavior<QueryActor.Message> {
	public interface Message{};

	// Enum class that represents a reading message to accept a value.
	public static final class QueryReading implements Message {
		final ActorSystem<BuildingManager.Command> toRequest;

		public QueryReading(ActorSystem<BuildingManager.Command> request) {
			this.toRequest = request;
		}
	}

	// Constructor class.
	private QueryActor(ActorContext<Message> context) {
		super(context);
	}

	/**
	 * Overriden createreceive method.
	 */
	@Override
	public Receive<Message> createReceive() {
		return newReceiveBuilder()
			.onMessage(QueryReading.class, this::querySystem)
			.build();
	}

	// Method that queries the device sensors to produce a response to a file.
	private Behavior<Message> querySystem(QueryReading reading) {
		ActorSystem<BuildingManager.Command> sys = reading.toRequest;
		System.out.println("Querying all actors");
		sys.tell(BuildingManager.QueryAllSensors.INSTANCE);
		return this;
	}
	
}
