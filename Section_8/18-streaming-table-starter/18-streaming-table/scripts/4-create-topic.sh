#!/bin/bash

# kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic stock-tick

${KAFKA_HOME}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic stock-tick

