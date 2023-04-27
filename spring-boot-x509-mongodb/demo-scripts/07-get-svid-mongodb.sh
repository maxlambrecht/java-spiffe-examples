#!/bin/bash

sudo -u mongodb bash <<EOF
  bin/spire-agent api fetch x509 -socketPath /tmp/spire-agent/public/api.sock -write .
  sleep 1
  cat svid.0.pem svid.0.key > mongodb.pem
  mv bundle.0.pem bundle.pem
  rm svid.0.pem svid.0.key
EOF
