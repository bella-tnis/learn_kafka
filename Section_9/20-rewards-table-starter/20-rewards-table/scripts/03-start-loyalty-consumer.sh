#!/bin/bash

${KAFKA_HOME}/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic loyalty --from-beginning