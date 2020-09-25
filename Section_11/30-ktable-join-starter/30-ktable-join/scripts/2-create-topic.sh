#!/bin/bash

${KAFKA_HOME}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 2 --topic user-master & ${KAFKA_HOME}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 2 --topic user-login