# Peer to peer Distributed File system 
## CMSC 626 Principles of computer security Group project

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)



## Team Members

- Girish Chandra Dama - WS95091
- Mohammad Akram - OO81170
- Zaheer Abdul - WF44252
- Sainath Mamidi - PB92577

## AIM

The project aims to develop a peer-to-peer distributed file system with encryption enabled for security, allowing peers to create, read, write, update, delete, and restore files on other peers.

## Architecture

The project has two entities - Peer and Master Server. The Peer acts as both client and server, while the Master Server stores information about all the files, including storage locations, deleted files, secret keys, and permissions.

## Design

The project uses RMI for communication between entities, a scheduled executor service to detect malicious activity, multithreading to handle concurrent requests, and AES encryption and decryption for secure data transfer. Permissions are managed using a Permission class, and files are never deleted but marked as deleted. The flow of file operations includes creating, reading, writing, updating, deleting, and restoring files.

## Flow of file operations

To create a file, the client establishes a connection with the Master, which returns the URIs of peers based on replication factor, allowing the file to be created at multiple peers. For reading, the Master checks if the peer has read permission and returns the key and peer URI of one of the peers containing the file. For write and update operations, the client connects with the Master to get peer URIs and secret keys and then invokes the operation on all the peers containing the file. To delete a file, the peer establishes a connection with the Master and deletes the file. To restore a file, the peer connects with the Master and invokes the restore function.
