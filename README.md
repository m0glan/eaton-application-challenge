# Eaton Application Challange

## Brief

The goal of this project is to implement a simulation in which one monitoring device and multiple measurement devices interact and exchange data; each client has an identification number. The challenge is for the monitoring device to count the number of messages it receives.

## Mechanism

This project uses standard Java socket programming, meaning a _client_ connecting via the `TCP` protocol to a _server_ having a certain address and running on a given port. On the level of the application layer, a _handshake_ based protocol has been put in place to make data-exchange between a client and the server possible and to facilitate smooth connection termination (thus avoiding brutal socket closure).

In addition to the previous features, a server model that supports multiple simultaneous connections has been implemented using Java multithreading through thread execution pools and synchronous methods in such a way that there can be no unwanted concurrent access to any critical section.

## Status

The main goal has been accomplished, _i.e._ a server that counts the number of messages it receives from clients that concurrently transfer data to it. Get version 1.0.0 [here](https://www.dropbox.com/sh/s8pwrjyb499h04v/AAAZwGOXr0h93-yHZZ4-I8mva?dl=0).
