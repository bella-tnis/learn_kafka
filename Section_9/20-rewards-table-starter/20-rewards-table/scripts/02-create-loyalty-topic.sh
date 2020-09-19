#!/bin/bash

${KAFKA_HOME}/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 2 --topic loyalty --config min.insync.replicas=2