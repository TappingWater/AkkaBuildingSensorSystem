# AkkaBuildingSensorSystem
Concurrent application built using the Akka toolkit to simulate an IOT system that tracks readings across multiple buildings

# How it works:
![Basic Diagram](https://ibb.co/3MnrrTS)

As can be seen in the above diagram. The sensor system that generates the readings that are consumed by the stream is done through
an actor system. The actor system follows the above hierachy. Akka provides a scheduler class that we can utilize to periodically tell
our actor system to post new readings to simulate real sensors. For the purpose of this simulation I have used 1 second but it can be changed.
We periodically send messages to our actor system while our stream is running to send new Device Info objects to our stream. The Device info
objects that get pushed onto the stream are then processed and printed using the logger.
