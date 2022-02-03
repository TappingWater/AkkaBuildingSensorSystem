package com.sensor.actors;

import java.util.HashMap;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Zone extends AbstractBehavior<Zone.Command> {
	public interface Command {}

	private final int zoneNum;
	private ActorRef<TemperatureSensor.Command> tempSensor;
	
	private Zone(ActorContext<Command> context, int zoneNo) {
		super(context);
		this.zoneNum = zoneNo;
		tempSensor = context.spawn(TemperatureSensor.create(), "temp");
	}

	// When we create our system we call the create behavior to create our system.
	public static Behavior<Command> create(int zoneNum) {
		return Behaviors.setup(context -> new Zone(context, zoneNum));
	}

	@Override
	public Receive<Command> createReceive() {		
		return null;
	}

}
