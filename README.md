[![Release](https://img.shields.io/badge/tag-v2.1.0-blue.svg)](https://github.com/vmoglan/eaton-application-challenge/releases) ![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)

# Eaton Application Challenge

## Description

The goal of this project is to implement a simulation in which one monitoring device and multiple measurement devices interact and exchange data; each client has an `id` attributed by the monitor. The challenge is for the monitor to count the number of messages it receives.

This project uses standard Java socket programming to establish connections between clients and a server. On the application layer, a _handshake based protocol_ has been put in place to make data-exchange between a client and the server possible and to facilitate smooth connection termination (thus avoiding brutal socket closure).

The interface gives control over the simulation in what concerns the number of active connections (cannot be bigger than `numberOfThreads + 1`) and the frequency at which the clients send messages to the server. It also displays the real-time message count (which does not change if there are no active connections).

## Download

Get [version 2.1.0 <img src="https://png.icons8.com/material-outlined/12/000000/downloading-updates.png">](https://github.com/vmoglan/eaton-application-challenge/releases/download/2.1.0/eac-2.1.0-binary.zip) (requires **Java 8**). 

## Building from source

Check the [wiki](https://github.com/vmoglan/eaton-application-challenge/wiki/Building-the-project-from-source) for information on how to build the project from source.
