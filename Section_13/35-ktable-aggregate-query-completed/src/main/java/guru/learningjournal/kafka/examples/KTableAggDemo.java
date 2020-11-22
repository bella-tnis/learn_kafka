package guru.learningjournal.kafka.examples;

import guru.learningjournal.kafka.examples.serde.AppSerdes;
import guru.learningjournal.kafka.examples.types.DepartmentAggregate;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class KTableAggDemo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        // define hostname and portnumber
        String hostName = (args.length < 2) ? "localhost" : args[0];
        int portNumber = (args.length < 2) ? 7010 : Integer.parseInt(args[1]);
        // make props for kafka streams
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, hostName + ":" + portNumber); // used this to define host info
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);

        // make stream builder
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        // fill the stream builder with app topology
        AppTopology.withBuilder(streamsBuilder);
        // build the streams
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        // make a query server
        AppRestService queryServer = new AppRestService(streams, hostName, portNumber);
        // state listener to assure the query server only active if the kafka broker is running
        streams.setStateListener((currentState, prevState) -> {
            logger.info("State Changing to " + currentState + " from " + prevState);
            queryServer.setActive(currentState == KafkaStreams.State.RUNNING && prevState == KafkaStreams.State.REBALANCING);
        });
        // start the sreams and query server
        streams.start();
        queryServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Streams");
            queryServer.stop();
            streams.close();
        }));
    }
}
