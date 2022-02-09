package com.sensor.streams;

import java.util.concurrent.CompletionStage;
import com.sensor.utility.DeviceInfo;
import akka.Done;
import akka.actor.ActorRef;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

/**
 * Class that represents the data stream processed by the application.
 * The stream take Device Info objects as input and filters them.
 * Before printing them to standard output as needed.
 */
public class DataStream {

	//Source object
	private static final Source<DeviceInfo, ActorRef> source = Source.actorRef(1000, OverflowStrategy.fail());
	//Simple sink object that uses the inbuilt toString() method of device info to print.
	private static final Sink<DeviceInfo, CompletionStage<Done>> logger = Sink.foreach(a->System.out.println(a));
	
	private static final FilterReadings filterFlow = new FilterReadings();

	//Runnable graph
	private static final RunnableGraph<ActorRef> graph = source.via(filterFlow).toMat(logger,Keep.left());

	public static Source<DeviceInfo, ActorRef> getSource() {
		return source;
	}	

	public static Sink<DeviceInfo, CompletionStage<Done>> getSink() {
		return logger;
	}	
	
	public static RunnableGraph<ActorRef> getStream() {
		return graph;
	}
}
