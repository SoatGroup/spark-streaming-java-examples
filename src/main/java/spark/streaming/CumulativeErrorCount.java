package spark.streaming;

/**
 * Created by mccstan on 02/05/17.
 */
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Serializable;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;


public class CumulativeErrorCount implements Serializable {

    public static void main(String[] args) throws Exception {

        SparkConf conf = new SparkConf().setMaster("local[2]")
                .setAppName("CumulativeErrorCount")
                .set("spark.serializer", KryoSerializer.class.getName());

//      Batch interval 5ms
        JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(1000));



//      CHECKPOINT, NEEDED FOR UPDATE STATE BY KEY OPERATION
        jssc.checkpoint("tmp/spark");


//      Define the socket where the system will listen
//      Lines is not a rdd but a sequence of rdd, not static, constantly changing
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream(args[0], Integer.parseInt(args[1]));


//      Split each line into words
        JavaDStream<String> words = lines.flatMap(
                (FlatMapFunction<String, String>) x -> Arrays.asList(x.split(" ")).iterator()
        );

//      Filter using the the lines containing errors
        JavaDStream<String> filteredWords = lines.filter(word -> word.contains("ERROR"));


//      Count each word in each batch
        JavaPairDStream<String, Integer> pairs = filteredWords.mapToPair(
                (PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1)
        );


// Update state BY key Function : A sum fonction
        Function2<List<Integer>, Optional<Integer>, Optional<Integer>> updateFunction =
                (Function2<List<Integer>, Optional<Integer>, Optional<Integer>>) (values, state) -> {
                    Integer newSum = values.stream().reduce(0, Integer::sum);
                    if (state != null && state.isPresent())
                        newSum += state.get();
                    return Optional.of(newSum);
                };


//      Cumulate the sum from each batch USING UPDATE STATE BY KEY
        JavaPairDStream<String, Integer> wordCounts = pairs.updateStateByKey(updateFunction);


// Print the first ten elements of each RDD generated in this DStream to the console
        wordCounts.print();

        jssc.start();
        jssc.awaitTermination();
    }


}
