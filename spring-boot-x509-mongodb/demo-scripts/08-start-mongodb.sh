#!/bin/bash

sudo -u mongodb bash <<EOF
nohup docker run --name mongodb -p 27017:27017 -v $(pwd)/conf/mongod.conf:/etc/mongod.conf \
        -v $(pwd)/mongodb.pem:/etc/ssl/certs/mongodb/mongodb.pem  \
        -v $(pwd)/bundle.pem:/etc/ssl/certs/mongodb/bundle.pem mongo:5.0 \
        --config /etc/mongod.conf > logs/mongodb.log &
EOF

sleep 5
echo "MongoDB is starting up... check logs/mongodb.log for details"