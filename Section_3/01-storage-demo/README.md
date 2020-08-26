# Storage Demo
This project is to demonstrate how to run Kafka and Zookeeper in linux environment.
We will make a cluster of Kafka broker consists of 3 Broker and 1 Zookeeper.
<br>

Assume we have installed kafka in `/usr/local/kafka` and zookeeper in `/usr/local/zookeeper`. We have stored this path in environment variable as `KAFKA_HOME` and `ZOOKEEPER_HOME`

**Kafka Server Configuration** <br>

Make 3 file of `server.properties` in `/usr/local/kafka/config`:
- `server-0.properties` with `broker.id = 0 ` 
- `server-1.properties` with `broker.id = 1`
- `server-2.properties` with `broker.id = 2`

Each of the file must have these properties. Change or add these properties as needed
```
# socket server listeners of kafka broker
# broker 0, 1, and 2 will be in port 9092, 9093, 9094 respectively.
# uncomment this properties and change the port.
listeners=PLAINTEXT://:9092

# for internal topic settings
offsets.topic.num.partitions=3
offsets.topic.replication.factor=3
transaction.state.log.num.partitions=3
transaction.state.log.replication.factor=3
transaction.state.log.min.isr=2
min.insync.replicas=2
default.replication.factor=3
```

### How to Run Apps <br>
1. Execute the script for starting the zookeeper `zookeeper-start.sh`
2. Execute the script for running the three kafka broker `0-kafka-server-start.sh, 1-kafka-server-start, 2-kafka-server-start`
3. Try to create a topic with script `topic-create.sh`. You will see this output
```
Created topic invoice.
```
4. You also can describe the topic using `describe-topic.sh`. You will see this output
```
Topic: invoice  PartitionCount: 5       ReplicationFactor: 3    Configs: segment.bytes=1000000
Topic: invoice  Partition: 0    Leader: 0       Replicas: 0,1,2 Isr: 0,1,2
Topic: invoice  Partition: 1    Leader: 1       Replicas: 1,2,0 Isr: 1,2,0
Topic: invoice  Partition: 2    Leader: 2       Replicas: 2,0,1 Isr: 2,0,1
Topic: invoice  Partition: 3    Leader: 0       Replicas: 0,2,1 Isr: 0,2,1
Topic: invoice  Partition: 4    Leader: 1       Replicas: 1,0,2 Isr: 1,0,2
```

If you can run these scripts smoothly, then your kafka and zookeeper configuration have running successfully. You are ready to make kafka streaming application!



