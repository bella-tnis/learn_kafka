#!/bin/bash

${KAFKA_HOME}/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic stock-tick --property parse.key=true --property key.separator=":"