# Eaton Application Challange

## Description

The goal of this project is to implement a simulation in which one monitoring device and multiple measurement devices, each having a unique name, interact and exchange data; the challenge is for the monitoring device to count the number of messages it receives. Since the subject does not specify what data the client devices measure, I decided to have them measure changes in a given folder and send the update information to the monitor (hence why they are called Directory Watchers).

## Status

The project is currently in development, with only the basic structure having been put in place. A test has been implemented to show the basic messages counting mechanism within a client-server architecture.

## Required Tools

For this project, once must install and set up the paths for [Git](https://git-scm.com/), [Java SE Development Kit 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and preferably [Maven 3.5.4](https://maven.apache.org/).

**NOTE:** Maven only works with `JDK` and not with `JRE` so you might need to set the `JAVA_HOME` environment variable (on Windows) to something such as `C:\Program Files\Java\jdk1.8.0_181`.

## Installation and Usage

### Dependency installation

You can grab this project by opening a terminal, navigating to your directory of choice and typing:

  `git clone https://github.com/vmoglan/eaton-application-challenge.git && cd eaton-application-challenge`
 
Once inside the project directory, the command `mvn clean install` installs the Maven dependencies, builds the project in development mode and runs all the tests; if you wish to run the tests in a specific Maven module, the command is `mvn clean test -pl module-name` while in the project directory. For more information on Maven commands, check the [introduction to the lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

### Running the example

To run the `Example.java` main class, while in the project's root directory do: `cd example && mvn exec:java -Dexec.mainClass="example.Example"` and that should do it (tested on Windows 10 and Ubuntu 18.04).
