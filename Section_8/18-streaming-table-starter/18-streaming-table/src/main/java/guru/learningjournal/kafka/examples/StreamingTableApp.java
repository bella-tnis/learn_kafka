package guru.learningjournal.kafka.examples;

import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;

import java.util.Properties;

public class StreamingTableApp {
    private static final Logger Logger = LogManager.getLogger();

    public static void main(final String[] args) {
        // make a new prop
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // make a stream builder
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        // make KTable from stream builder with topic: stocl-tick
        KTable<String, String> KT0 = streamsBuilder.table(AppConfigs.topicName);
        KT0.toStream().print(Printed.<String, String>toSysOut().withLabel("KT0"));

        // filter out the data, we just want the key with value of HDFCBANK or TCS
        KTable<String, String> KT1 = KT0.filter(
                // key value lamda expression
                (k, v) -> k.matches(AppConfigs.regExSymbol) && !v.isEmpty(),
                // name of the state store
                Materialized.as(AppConfigs.stateStoreName));
        KT1.toStream().print(Printed.<String, String>toSysOut().withLabel("KT1"));

        // build the stream
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);

        //Query Server serves http://localhost:7010/kt01-store/all
        QueryServer queryServer = new QueryServer(streams, AppConfigs.queryServerHost, AppConfigs.queryServerPort);
        streams.setStateListener((newState, oldState)->{
            Logger.info("State changing to " + newState + " from " + oldState);
            queryServer.setActive(newState == KafkaStreams.State.RUNNING && oldState == KafkaStreams.State.REBALANCING);
        });

        // start the sream and query server
        streams.start();
        queryServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            Logger.info("Shutting down servers...");
            queryServer.stop();
            streams.close();
        }));

    }
}
