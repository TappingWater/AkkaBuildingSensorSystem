package com.sensor.streams;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.sensor.utility.DeviceInfo;
import com.sensor.utility.TemperatureConfig;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.japi.Pair;
import akka.japi.pf.PFBuilder;
import akka.stream.ClosedShape;
import akka.stream.Outlet;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class DataStream {

	private static final Source<DeviceInfo, ActorRef> source = Source.actorRef(1000, OverflowStrategy.fail());
	private static final Flow<DeviceInfo, DeviceInfo, NotUsed> filterTempReadings = Flow.of(
		DeviceInfo.class).collect(
			new PFBuilder<DeviceInfo, DeviceInfo>().match(
				DeviceInfo.class,
				(info)->info.getReading()<TemperatureConfig.lowerLimit,
				(info)->info
		).build());
	private static final Sink<DeviceInfo, CompletionStage<Done>> logger = Sink.foreach(a->System.out.println(a));
	private static ActorRef sourceRef = null;

	private static final RunnableGraph<CompletionStage<Done>> graph = RunnableGraph.fromGraph(GraphDSL.create(
		logger, 		
		(builder, out)->{
			final Outlet<DeviceInfo> outlet = builder.add(source).out();
			builder.from(outlet).via(builder.add(filterTempReadings)).to(out);
			return ClosedShape.getInstance();
		}
	));

	public static Source<DeviceInfo, ActorRef> getSource() {
		return source;
	}	

	public static Sink<DeviceInfo, CompletionStage<Done>> getSink() {
		return logger;
	}	
	
	public static RunnableGraph<CompletionStage<Done>> getStream() {
		return graph;
	}

	public static void generateSourceRef(ActorSystem system) {
		Pair<ActorRef, Source<DeviceInfo, NotUsed>> actorRefSourcePair = source.preMaterialize(system);
		sourceRef = actorRefSourcePair.first();
	}
	
	public static Optional<ActorRef> getSourceRef() {		
		if (sourceRef != null) {
			return Optional.of(sourceRef);
		}
		return Optional.empty();
	}
}
