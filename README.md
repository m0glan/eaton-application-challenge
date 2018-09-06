# Eaton Application Challange

## Description

The goal of this project is to implement a simulation in which one monitoring device and multiple measurement devices, each having a unique name, interact and exchange data; the challenge is for the monitoring device to count the number of messages it receives. 

## Status

The project is currently in development, with only the basic structure having been put in place. A test has been implemented to show the basic messages counting mechanism within a client-server architecture.

## Required Tools

For this project, once must install and set up the paths for [Git](https://git-scm.com/), [Java SE Development Kit 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and preferably [Maven 3.5.4](https://maven.apache.org/).

## Installation and Usage

You can grab this project by opening a terminal, navigating to your directory of choice and typing:

  `git clone https://github.com/vmoglan/eaton-application-challenge.git && cd eaton-application-challenge`
 
Once inside the project directory, you can run all the tests using `mvn clean test` or, if you want to run the tests in one module, `mvn clean test -pl module-name`. The console will display something similar to `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`. These instructions have been tested on Windows 10 and Ubuntu 18.04.
