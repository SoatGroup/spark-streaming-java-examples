# Get started with Spark Streaming
The java guide.

## Installation
You'll need Spark, maven and java.
Follow this tutorial to install spark :
https://www.tutorialspoint.com/apache_spark/apache_spark_installation.htm




## WordCount
### spark.Streaming.WordCount
*A stateless operation*

Count the words that occurs instantanously.


## BasicErrorCount
### spark.Streaming.BasicErrorCount
*A stateless operation*

Count the Errors that occurs instantanously.


## BasicErrorCount
### spark.Streaming.CumulativeErrorCount
*A statefull operation*

Count the whole Errors that occured since the beginning.


## BasicErrorCount
### spark.Streaming.WindowAndKeyErrorCount
*A statefull operation*

Count the whole Errors that occured within a sliding window of time.


## Running the examples 
To run the examples you have to open a scoket where we will write text to simulate input streams.
### Open a socket
Open a socket on port 9999 using netcat utility.
```shell
$ nc -lk 9999
```

### Build the project
Clone the project, get inside and run on a shell
```shell
$ mvn install
$ mvn clean package
```

### Run the streaming App
Using spark-submit
```shell
$ spark-submit --class spark.streaming.Wordcount target/spark-streaming-examples-1.0.jar localhost 9999
```
To launch the other examples, just change the class name : *--class spark.streaming.ClassName*


### Test
On the shell where netcat was launch, write textes
```shell
I'm writing text
it is fun
```
### Results
You'll see the processing results instantanously.


### SparUI
When the Streaming app is running, you could monitor using the spark UI : http://localhost:4040/
