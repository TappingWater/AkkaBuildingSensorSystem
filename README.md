# AkkaBuildingSensorSystem
Concurrent application built using the Akka toolkit to simulate an IOT system that tracks readings across multiple buildings

# Problem:
These days we have a lot of skyscrapers, warehouses and large buildings with multiple floors. A common problem when it comes 
to large buildings or even groups of buildings belonging to the same entity is how to monitor variables that can vary from 
one point to another such as temperature, humidity and illumination for storage or comfort purposes. To ensure we maintain 
a standard across multiple floors we need to account for variables that could vary from floor to floor and even within different
areas in the same floor. For example, sunlight hitting the building at different angles could result in different light
levels for different areas and floors.

# Proposed Solution:
Implement an IOT system for the buildings using groups of sensors. Each building can have multiple floors and the floors themselves
can be large such that there might be multiple varied reading across the same floor. For this reason it is best to split the floors 
to zones as well. Each zone can then have a dedicated group of sensors that upload their readings to our system for processing through
a stream. When these readings don't align with the limits we can then publish a message to our system that indicates exactly where in 
our building and what reading needs to be adjusted to meet our required standards.

# Why Akka:
Akka is a toolkit that provides boilerplate code to implement easier concurrent systems. In this system each device sensor will upload
readings to our stream. We need a system to manage our hierachy(building>floor>zone) as well as make use of multiple threads because we
don't need to execute the code for our readings synchronously. By using Akka we can build an actor system that can manage multiple device
sensors across multiple threads and simulate sending their readings to our stream similar to a real world scenario. Akka also provides a
great way to process streams. We can forward the readings we get from our actor system to our stream which will run paralelly and process
any readings that are sent by the actors.

# Features:
* Concurrent application.
* Initialize application using user input.
* Use of akka actors to simulate device sensors.
* Use of akka actor hierachy to map entities such as buildings, floors, zones and sensors.
* Use of inbuilt akka stream components like sources, flows and sinks to implement a runnable graph.(Akka's version of a blueprint for a stream)
* Use of akka streams to implement a custom flow component to filter readings from device sensor actors.

# How it works:
![Basic Diagram](https://i.ibb.co/D8ZVV1w/Building-Sensor.jpg)

As can be seen in the above diagram. The sensor system that generates the readings that are consumed by the stream is done through
an actor system. The actor system follows the above hierachy. Akka provides a scheduler class that we can utilize to periodically tell
our actor system to post new readings to simulate real sensors. For the purpose of this simulation I have used 1 second but it can be changed.
We periodically send messages to our actor system while our stream is running to send new Device Info objects to our stream. The Device info
objects that get pushed onto the stream are then processed and printed using the logger.
