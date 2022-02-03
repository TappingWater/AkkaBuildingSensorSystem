package com.sensor.actors;

import java.time.Duration;
import java.util.Optional;
import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.stream.javadsl.Source;

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
		final long requestId;
		// In our case we are sending data to a stream but streams in akka also have an
		// actor ref.
		final ActorRef<CurrentTemp> replyTo;

		public GetTemp(long requestId, ActorRef<CurrentTemp> replyTo) {
			this.requestId = requestId;
			this.replyTo = replyTo;
		}
	}

	// Static class that represents the message sent to the stream which has the
	// requestId and the value.
	public static final class CurrentTemp {
		final long requestId;
		// Returns a value if something has been recorded in an optional object.
		final Optional<Double> value;

		public CurrentTemp(long requestId, Double value) {
			this.requestId = requestId;
			this.value = value;
		}
	}

	// To create a new sensor, we need to consider certain factors such as building,
	// floor and zone
	// Recommended to create new devices using setup() method.
	public static Behavior<Command> create(String buildingName, String floor, String zone) {
		return Behaviors.setup(context -> new TemperatureSensor(context, buildingName, floor, zone));
	}

	private final String building;
	private final String floor;
	private final String zone;
	private Optional<Double> prevTemp = Optional.empty();
	private static final Double upperLimit = 76.00;
	private static final Double lowerLimit = 64.00;
	private Source<Double, Cancellable> randomizedTempSource = Source.tick(Duration.ofSeconds(1),
			Duration.ofSeconds(1), (Double) ((Math.random() * (upperLimit - lowerLimit)) + lowerLimit));

	// Constructor defined as private. New sensor actors created using create.
	private TemperatureSensor(ActorContext<Command> context, String building, String floor, String zone) {
		super(context);
		this.building = building;
		this.floor = floor;
		this.zone = zone;
		
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
		g.replyTo.tell(new CurrentTemp(g.requestId, return randomizedTempSource));
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
