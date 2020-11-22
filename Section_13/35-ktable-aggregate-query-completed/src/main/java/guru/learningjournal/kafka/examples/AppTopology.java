package guru.learningjournal.kafka.examples;

import guru.learningjournal.kafka.examples.serde.AppSerdes;
import guru.learningjournal.kafka.examples.types.DepartmentAggregate;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;

public class AppTopology {
    static void withBuilder(StreamsBuilder builder) {
        builder.table(AppConfigs.topicName,
            Consumed.with(AppSerdes.String(), AppSerdes.Employee()))
            .groupBy((k, v) -> KeyValue.pair(v.getDepartment(), v), Grouped.with(AppSerdes.String(), AppSerdes.Employee()))
            .aggregate(
                //Initializer
                () -> new DepartmentAggregate()
                        .withEmployeeCount(0)
                        .withTotalSalary(0)
                        .withAvgSalary(0D),
                //Adder
                (k, v, aggV) -> new DepartmentAggregate()
                        .withEmployeeCount(aggV.getEmployeeCount() + 1)
                        .withTotalSalary(aggV.getTotalSalary() + v.getSalary())
                        .withAvgSalary((aggV.getTotalSalary() + v.getSalary()) / (aggV.getEmployeeCount() + 1D)),
                //Subtractor
                (k, v, aggV) -> new DepartmentAggregate()
                        .withEmployeeCount(aggV.getEmployeeCount() - 1)
                        .withTotalSalary(aggV.getTotalSalary() - v.getSalary())
                        .withAvgSalary((aggV.getTotalSalary() - v.getSalary()) / (aggV.getEmployeeCount() - 1D)),
                //Serializer
                Materialized.<String, DepartmentAggregate, KeyValueStore<Bytes, byte[]>>as(
                        AppConfigs.stateStoreName).withValueSerde(AppSerdes.DepartmentAggregate())
            ).toStream().print(Printed.<String, DepartmentAggregate>toSysOut().withLabel("Department Aggregate"));
    }
}
