![GitHub release](https://img.shields.io/badge/release-1.0.0-green.svg) ![Licence](https://img.shields.io/packagist/l/doctrine/orm.svg) 



# Eaton Application Challange

## Brief

The goal of this project is to implement a simulation in which one monitoring device and multiple measurement devices interact and exchange data; each client has an identification number. The challenge is for the monitoring device to count the number of messages it receives.

## Mechanism

This project uses standard Java socket programming, meaning a _client_ connecting via the `TCP` protocol to a _server_ having a certain address and running on a given port. On the level of the application layer, a _handshake_ based protocol has been put in place to make data-exchange between a client and the server possible and to facilitate smooth connection termination (thus avoiding brutal socket closure). An important thing to note is that clients/measuring devices start out with their identifiers set to -1: it is through the initial connection that they ask the server to attribute them a unique ID.

In addition to the previous features, a server model that supports multiple simultaneous connections has been implemented using Java multithreading through thread execution pools and synchronous methods in such a way that there can be no unwanted concurrent access to any critical section.

## Download

Get version 1.0.0 [<img src="https://png.icons8.com/material-outlined/12/000000/downloading-updates.png">](https://www.dropbox.com/sh/s8pwrjyb499h04v/AAAZwGOXr0h93-yHZZ4-I8mva?dl=0). Check the [wiki](https://github.com/vmoglan/eaton-application-challenge/wiki/Building-the-project-from-source) for information on how to build the project from source.
