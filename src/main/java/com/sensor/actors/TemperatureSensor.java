package com.sensor.actors;

import com.sensor.streams.DataStream;
import com.sensor.utility.DeviceInfo;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


/**
 * Actor object that represents a temperature sensor.
 */
public class TemperatureSensor extends AbstractBehavior<TemperatureSensor.Command> {

	// Nested marker interface to represent the types of messages that the sensor
	// can process.
	public interface Command {
	}

	// Messages for actors should be immutable. declaring a class as static final
	// insures us that the message would only be created once as a singleton class
	// and only one thread can access it at a time.
	// Represents a message that will be accepted by this actor to get the
	// temperature.
	public static final class GetTemp implements Command {
		final ActorRef<CurrentTemp> replyTo;

		public GetTemp(ActorRef<CurrentTemp> replyTo) {
			this.replyTo = replyTo;
		}
	}

	// Static class that represents the message sent to the stream which has the
	// requestId and the value.
	public static final class CurrentTemp {
		final long requestId;
		// Returns a value if something has been recorded in an optional object.
		final Double value;

		public CurrentTemp(long requestId, Double value) {
			this.requestId = requestId;
			this.value = value;
		}
	}

	// To create a new sensor, we need to consider certain factors such as building,
	// floor and zone
	// Recommended to create new devices using setup() method.
	public static Behavior<Command> create(String buildingName, int floor, int zone) {
		return Behaviors.setup(context -> new TemperatureSensor(context, buildingName, floor, zone));
	}

	private final String building;
	private final int floor;
	private final int zone;

	// Constructor defined as private. New sensor actors created using create.
	private TemperatureSensor(ActorContext<Command> context, String buildingName, int floor, int zone) {
		super(context);
		this.building = buildingName;
		this.floor = floor;
		this.zone = zone;
		postReadings();
	}

	/**
	 * Queue readings to source periodically
	 * 
	 * @return
	 */
	private void postReadings() {
		while (true) {
			if (DataStream.getSourceRef().isPresent()) {					
				Double reading = 80.0;
				DataStream.getSourceRef().get().tell(new DeviceInfo(building, floor, zone, reading, "Temp"), null);
			}
		}
	}

	/**
	 * Default method for an akka actor that needs to be overriden.
	 * We create a new receive builder object where we can match the message types
	 * our actor receives and how we process them and change the state of actor if
	 * needed.
	 */
	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
				.onMessage(GetTemp.class, this::getTemp)
				.onSignal(PostStop.class, signal -> onPostStop())
				.build();
	};

	/**
	 * Behavior called by actor to get temperature for the GetTemp class.
	 * 
	 * @param g
	 *          GetTemp Message that will be sent to the actor to get a response.
	 * @return
	 */
	private Behavior<Command> getTemp(GetTemp g) {
		// g.replyTo.tell(new CurrentTemp(g.requestId, return randomizedTempSource));
		return this;
	}

	/**
	 * Method called when we need to shutdown the device gracefully.
	 * 
	 * @return
	 */
	private Behavior<Command> onPostStop() {
		System.out.printf("\n Temperature sensor for zone %s in floor %s of building %s has been shut down.", zone,
				floor, building);
		return Behaviors.stopped();
	}

}
