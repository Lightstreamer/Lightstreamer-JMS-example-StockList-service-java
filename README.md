# Lightstreamer JMS Extender - Stock-List Demo - Java (JMS) Service

This project contains the source code and all the resources needed to install the Stock-List Demo Service for Lightstreamer JMS Extender.

The Stock-List Demo Service is a stand-alone Java application that acts as a service, sending simulated stock quotes to a JMS topic. It provides data for client-side Stock-List Demos for JMS Extender, such as the [HTML Demo](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-javascript) and the [Node.js Demo](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-node).

Note that this service makes no use of the JMS Extender APIs: it works by using directly the JMS APIs. For simplicity, a single JMS topic is used for all stocks while, in a real scenario, you would probably use a different JMS topic for each stock.

Check out the sources for further explanations.

## Install

If you want to install a version of this demo pointing to your local [Lightstreamer JMS Extender](https://lightstreamer.com/download/#jms200), follow these steps:

### Configure the JMS Broker

As a prerequisite, this demo needs a JMS infrastructure to run.
You can choose whatever JMS broker you prefer to be used for this example.
We will show 3 examples using **Apache ActiveMQ**, **Apache ActiveMQ Artemis**, and **TIBCO EMS**:

#### Apache ActiveMQ

There's no need to create topics or queues because [ActiveMQ](http://activemq.apache.org/components/classic/) supports the dynamic configuration of destinations.

#### Apache ActiveMQ Artemis

There's no need to create topics or queues because [ActiveMQ Artemis](http://activemq.apache.org/components/artemis/) supports the dynamic configuration of destinations.


#### TIBCO EMS

1. You should create a new topic. Open the `topics.conf` file and append to it `stocksTopic`.

2. Copy `tibjms.jar` from `<EMS_HOME>/lib` to the `local_libs` folder.


**_NOTE_**: In the case of a different JMS broker, please follow the relative instructions on how to properly configure a topic. Furthermore, add an entry to the `dependencies` section in the `build.gradle` (read the
inline comments for more details).

### Configure the Demo Service

To configure the demo service, follow these steps:

1. Edit the set of settings as per the configuration of the selected JMS broker in the `src/main/resources/service.conf` file. If you want to use a different broker, add the relative settings following the provided samples. All parameters are mandatory.

2. Check the logging configuration file `src/main/resources/logback.xml`.

### Build the Demo Service

To build the demo service, run *Gradle* as follows:

```sh
$ ./gradlew build -Pbroker=<JMS Broker>
```

where the `broker` property can be:

* `ActiveMQ`
* `Artemis`
* `TIBCO`
* the `<JMS_BROKER>` string you specified in the `dependencies` section of `build.gradle` in the case of different JMS broker.

The `build` task will create a distribution of the project under the `build/install/stocklist-demo-service` folder, from which you can start the service.

Moreover, the `build/deploy` folder will be populated with all the JMS broker-specific jar files to be deployed into your local JMS Extender installation. In addition, `feedmessage-X.Y.Z.jar` will contain the `FeedMessage` class required by the Extender to create any object sent or received by its clients.

### Start the Demo Service

From the `build/install/stocklist-demo-service` directory, run the launch script to start sending simulated messages to the JMS broker:

```sh
$ ./bin/stocklist-demo-service
```

or launch `bin/stocklist-demo-service.bat` on Windows systems.


### Set up Lightstreamer JMS Extender

1. The JMS Extender StockList Demo requires a Lightstreamer JMS Extender instance running. Please refer to the Lightstreamer web site [download page](http://download.lightstreamer.com/) to find Lightstreamer JMS Extender download packages.

2. Now copy the jar files from `build/deploy` to the `<JMS_EXTENDER_HOME>/jms_connectors/lib` folder.

3. Edit the `<JMS_EXTENDER_HOME>/jms_connectors/jms_connectors_conf.xml` file, ensuring that the sample JMS connector configuration relative to the selected JMS broker is enabled. In particular, check that the `disabled` parameter is set to `false`:

    ```xml
    <param name="disabled">false</param>
    ```
    **_NOTE_**: Add a new JMS connector configuration in case you want to use a different JMS broker: see the inline comments for details.

Now you can test this demo by running the [Lightstreamer JMS Extender - Basic Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-javascript).

## See Also

### Clients Using This Service

* [Lightstreamer JMS Extender - Basic Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-javascript)
* [Lightstreamer JMS Extender - Basic Stock-List Demo - Node.js Client](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-node)
* [Lightstreamer JMS Extender - Basic Stock-List Demo - Java SE Client](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-java)
* [Lightstreamer JMS Extender - Basic Stock-List Demo - Android Client](https://github.com/Lightstreamer/Lightstreamer-JMS-example-StockList-client-android)

### Related Projects

* [Lightstreamer JMS Extender - Portfolio Demo - Java (JMS) Service](https://github.com/Lightstreamer/Lightstreamer-JMS-example-Portfolio-service-java)

## Lightstreamer Compatibility Notes

* Compatible with Lightstreamer JMS Extender since version 2.0.0 or newer.
