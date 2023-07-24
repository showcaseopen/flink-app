#!/bin/bash

# Parameters
HOST_IP=$1
KEY_PATH="<path-to-your-private-key>"
USERNAME="Raju"
USER_HOME="/home/${USERNAME}"
JAR_PATH="${USER_HOME}/flink/job.jar"

# Set up the SSH tunnel in the background
ssh -f -N -L 8081:localhost:8081 -i "${KEY_PATH}" ${USERNAME}@${HOST_IP}

# Give some time to establish the connection
sleep 10

# Upload the jar and start the job
JAR_ID=$(curl -s -X POST -H "Expect:" -H "Content-Type: multipart/form-data" -F "jarfile=@${JAR_PATH}" http://localhost:8081/jars/upload | jq -r .filename)

curl -X POST http://localhost:8081/jars/${JAR_ID}/run

# Kill the SSH tunnel
TUNNEL_PID=$(ps aux | grep 'ssh' | grep '8081:localhost:8081' | awk '{print $2}')
kill ${TUNNEL_PID}
