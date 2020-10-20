# Java SPIFFE examples

A bunch of [java-spiffe](https://github.com/spiffe/java-spiffe) use examples

## Before running the examples

To run the examples, a SPIRE Server and Agent need to be running.

### Run SPIRE

Download the latest SPIRE from [releases github page](https://github.com/spiffe/spire/releases) and extract the tar file.

Copy the content of [spire/conf](spire/conf) in the SPIRE conf folder. 

#### Run SPIRE Server

In a terminal in the SPIRE root folder run:

```
./spire-server run
```

#### Create SPIRE Registration Entry

```
./spire-server entry create -parentID spiffe://example.org/host -spiffeID spiffe://example.org/myservice -ttl 300 -selector unix:uid:1000
```

Change the UID to the UID of the user you will use to run the examples.

For the sake of simplicity the example uses the same identity (`myservice`) for both the frontend and backend apps.

#### Run SPIRE Agent

Create a join token:
```
TOKEN=$(./spire-server token generate -spiffeID spiffe://example.org/host | awk -F'Token: ' '{print $2}')
```

Start the agent using the join token to connect to the server:

```
./spire-agent run -joinToken $TOKEN
```

#### Define environment variable to point to Socket Endpoint

```
export SPIFFE_ENDPOINT_SOCKET=unix:/tmp/agent.sock
```

This environment variable is used by the java-spiffe library as a default socket endpoint address to connect to the SPIRE Agent.


