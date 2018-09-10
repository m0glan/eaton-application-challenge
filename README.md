# Eaton Application Challange

## Brief

The goal of this project is to implement a simulation in which one monitoring device and multiple measurement devices interact and exchange data; each client has an identification number. The challenge is for the monitoring device to count the number of messages it receives.

## Status

The project is currently in development, with only the basic goal having been accomplished, _i.e_ a server that counts the number of messages it receives from clients that concurrently transfer data to it.

## Mechanism

This project uses standard Java socket programming, meaning a _client_ connecting via the `TCP` protocol to a _server_ having a certain address and running on a given port. On the level of the application layer, a _handshake_ based protocol has been put in place to make data-exchange between a client and the server possible and to facilitate smooth connection termination (thus avoiding brutal socket closure).

In addition to the previous features, a server model that supports multiple simultaneous connections has been implemented using Java multithreading through thread execution pools and synchronous methods in such a way that there can be no unwanted concurrent access to any critical section.
