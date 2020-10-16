# Java SPIFFE examples

A bunch of [java-spiffe](https://github.com/spiffe/java-spiffe) use examples

## Before running the examples

To run this example, a SPIRE Server needs to be running configured with a `NodeAttestor "join_token"` (use the config file
that is bundled with the distribution). The agent is configured with a `WorkloadAttestor "unix"`.

#### Create SPIRE Registration Entry

```
./spire-server entry create -parentID spiffe://example.org/host -spiffeID spiffe://example.org/myservice -ttl 300 -selector unix:uid:1000
```

Change the UID to the one you will use to run the examples.

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

