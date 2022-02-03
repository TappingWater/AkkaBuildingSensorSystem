package com.sensor.streams;

import akka.actor.ActorRef;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;

public class TempSource {

	private static final int bufferSize = 1000;

	private static final Source<Object, ActorRef> source = Source.actorRef(Integer.MAX_VALUE, OverflowStrategy.dropTail());

	private TempSource() {}
	
	public static Source<Object, akka.actor.ActorRef> getSourceRef() {
		return source;
	}
}
