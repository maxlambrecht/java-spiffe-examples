#!/bin/bash

echo "Creating user for MongoDB client with subject: DC=dbuser,CN=dbuser,O=SPIRE,C=US"

# add x509-authenticated user to MongoDB
docker exec -ti mongodb /bin/bash -c "mongo --tls --tlsCertificateKeyFile /etc/ssl/certs/mongodb/mongodb.pem --tlsCAFile /etc/ssl/certs/mongodb/bundle.pem --tlsAllowInvalidHostnames --eval 'db.getSiblingDB(\"\$external\").runCommand({createUser: \"DC=dbuser,CN=dbuser,O=SPIRE,C=US\", roles: [{ role: \"readWrite\", db: \"test\" }], writeConcern: { w: \"majority\" , wtimeout: 5000 }})'"


