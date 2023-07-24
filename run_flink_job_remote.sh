#!/bin/bash

# The name of your Flink job's Docker service
SERVICE_NAME="jobmanager"

# The class name of your Flink job
CLASS_NAME="com.raju.Main"

# The path to your job jar inside the Docker container
JAR_PATH="/opt/flink/usrlib/myjob.jar"

# The username on your EC2 instance
USERNAME="Raju"

# The IP address of your EC2 instance
IP_ADDRESS=$1 # pass as argument when running script

# Get the ID of the first container for the specified service
CONTAINER_ID=$(docker ps --filter "name=${SERVICE_NAME}" --format "{{.ID}}" | head -n 1)

if [ -z "$CONTAINER_ID" ]
then
  echo "No containers running for service ${SERVICE_NAME}"
  exit 1
fi

# Run the Flink job inside the Docker container over SSH
ssh -L 8081:localhost:8081 -N -f -l $USERNAME $IP_ADDRESS
ssh -t $USERNAME@$IP_ADDRESS 'docker exec -it $CONTAINER_ID /bin/bash -c "/opt/flink/bin/flink run -c $CLASS_NAME $JAR_PATH"'
