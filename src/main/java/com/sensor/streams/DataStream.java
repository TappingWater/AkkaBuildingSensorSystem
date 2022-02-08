package com.sensor.streams;

import java.util.concurrent.CompletionStage;
import com.sensor.utility.DeviceInfo;
import com.sensor.utility.ReadingConfig;
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.japi.pf.PFBuilder;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
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
	//Flow object of stream to filter device info objects.
	private static final Flow<DeviceInfo, DeviceInfo, NotUsed> filterTempReadings = Flow.of(
		DeviceInfo.class).collect(
			new PFBuilder<DeviceInfo, DeviceInfo>().match(
				DeviceInfo.class,
				(info)->info.getReading()<ReadingConfig.tempLower||info.getReading()>ReadingConfig.tempUpper,
				(info)->info
		).build());
	//Simple sink object that uses the inbuilt toString() method of device info to print.
	private static final Sink<DeviceInfo, CompletionStage<Done>> logger = Sink.foreach(a->System.out.println(a));
	
	//Runnable graph
	private static final RunnableGraph<ActorRef> graph = source.via(filterTempReadings).toMat(logger,Keep.left());

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
