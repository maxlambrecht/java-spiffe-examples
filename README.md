# Java SPIFFE examples

A bunch of [java-spiffe](https://github.com/spiffe/java-spiffe) use examples


## Prerequisites

* A `spire-server` and a `spire-agent` running, providing an identity with
a SPIFFE ID = `spiffe://example.org/myservice`.

* A `SPIFFE_ENDPOINT_SOCKET` variable defined in the environment pointing to
agent endpoint: `unix:/tmp/agent.cok`
