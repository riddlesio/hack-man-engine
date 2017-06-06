# Booking Game
This repository contains the engine for the Booking.com Hack-man game for the Riddles.io platform.

## Setting up

This guide assumes the following software to be installed and globally
accessible:

- Gradle 2.14
- JVM 1.8.0_91

## Opening this project in IntelliJ IDEA

- Select 'Import Project'
- Browse to project directory root
- Select build.gradle
- Check settings:
- * Use local gradle distribution
- * Gradle home: /usr/share/gradle-2.14
- * Gradle JVM: 1.8
- * Project format: .idea (directory based)

*Note: for other IDEs, look at online documentation*

## Building the engine using IntelliJ IDEA

Use Gradle to build a .jar of the engine. Go to Tasks -> build -> jar.  
The .jar file can be found at `build/libs/bookinggame-2.0.0.jar`.

## Building the engine using Docker

If you have docker installed on your system you can build the engine using gradle inside
a docker container with your current path mounted inside the container so that the engine
.jar file is easily accesible. From the repository root directory run:
```
docker build -t gradle-hackman ./docker
docker run -v $(pwd):/gradle-app gradle-hackman:latest gradle build
```

## Running 

Running is handled by the GameWrapper. This application handles all communication between
the engine and bots and stores the results of the match. To run, firstly edit the 
`wrapper-commands.json` file. This should be pretty self-explanatory. Just change the command
fields to the right values to run the engine and the bots. In the example, the starterbot
is run twice, plus the command for the engine built in the previous step.
 
 To run the GameWrapper, use the following command (Linux):
 ````
 java -jar game-wrapper.jar "$(cat wrapper-commands.json)"
 ````
 
 *Note: if running on other systems, find how to put the content of wrapper-commands.json as
 argument when running the game-wrapper.jar*
