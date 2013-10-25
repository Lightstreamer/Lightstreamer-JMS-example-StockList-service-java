# Lightstreamer JMS Gateway StockList Demo Service for Java #

This project contains the source code and all the resources needed to install the StockList Demo Service for Lightstreamer JMS Gateway.<br>

Check out the sources for further explanations.

# Build #

If you want to skip the build process of this Service please note that in the [deploy release](https://github.com/Weswit/Lightstreamer-jms-example-StockList-service-java/releases) of this project you can find the "deploy.zip" file that contains a ready-made deployment resource for the Lightstreamer server.<br>

# Deploy #

In order to install the example you have to configure a JMS broker and deploy the service components.

## JMS broker ##

You can choose a JMS broker to be used for this example. We will show 4 examples using <b>HornetQ (AKA JBoss Messaging)</b>, <b>TIBCO EMS</b>, <b>Apache ActiveMQ</b> and <b>JBossMQ</b>.<br>
Once you have selected and installed your JMS broker, you will have to configure it accordingly to other components, in particular:
- you will have to copy JMS broker-specific jars in the adapter directory and in the data generator directory
- you will have to select the specific connection parameters in the adapter configuration, in the data generator and in the demo page

### With HornetQ (AKA JBoss Messaging) ###

1) You should configure a topic and a queue. Open the hornetq-jms.xml located under [HornetQHome](http://www.jboss.org/hornetq)"/config/stand-alone/non-clustered" and add the following nodes:

```xml

   <topic name="stocksTopic">
      <entry name="stocksTopic"/>
   </topic>

   <queue name="stocksQueue">
      <entry name="stocksQueue"/>
   </queue>

```

2) Copy the following jars: "hornetq-commons.jar", "hornetq-core-client.jar", "hornetq-jms-client.jar", "jnp-client.jar" and "netty.jar" from "/HornetQHome/lib". You will need to paste them later.

### With TIBCO EMS ###

1) You should create a topic and a queue. Open the queues.conf and topics.conf located under [EMSHome](http://www.tibco.com/products/automation/messaging/enterprise-messaging/enterprise-message-service/default.jsp)"/bin/" and append to them the lines containing (without apexes) "stocksQueue" for queues.conf and "stocksTopic" for topics.conf.

2) Copy "tibcrypt.jar", "tibjms.jar" and "tibjmsufo.jar" from "EMSHome/clients/java". You will need to paste them later.

### With Apache ActiveMQ ###

1) There's no need to create topics or queues, since [ActiveMQ](http://activemq.apache.org/) supports dynamic configuration of destinations.

2) Copy the following jars: "activemq-core-5.6.0.jar", "geronimo-j2ee-management_1.1_spec-1.0.1.jar" and "slf4j-api-1.6.4.jar", from "/ActiveMQHome/lib", changing the version number accordingly to your installation. You will need to paste them later.

### With JBossMQ ###

1) You shold create a topic and a queue. Open the jbossmq-destinations-service.xml located under [JBossHome](http://www.jboss.org/products/amq)"/server/default/deploy/jms/ and add two mbean nodes as shown below:

```xml

	<mbean code="org.jboss.mq.server.jmx.Topic"
		name="jboss.mq.destination:service=Topic,name=stocksTopic">
		<depends optional-attribute-name="DestinationManager">jboss.mq:service=DestinationManager</depends>
	</mbean>

	<mbean code="org.jboss.mq.server.jmx.Queue"
		name="jboss.mq.destination:service=Queue,name=stocksQueue">
    	<depends optional-attribute-name="DestinationManager">jboss.mq:service=DestinationManager</depends>
	</mbean>

```

2) Copy "jbossall-client.jar" from "JBossHome/client/". You will need to paste it later.

## The JMS Gateway adapter ##

Please copy from the [JMS Gateway zip](http://www.lightstreamer.com/download) file the adapter directory ("JMSGateway") under "LightstreamerHome/adapters" directory. Paste previously copied jars under JMSGateway/lib.

Please also download the JMS distribution version 1.1 from Oracle's Java website. It cannot be distributed with the JMS Gateway for licensing issues. It can be found here: [http://www.oracle.com/technetwork/java/docs-136352.html](http://www.oracle.com/technetwork/java/docs-136352.html).
Extract its content and find the jms.jar file under the lib directory. Please copy this jar under JMSGateway/lib.

Since the adapter will be receiving object messages coming from the generator, you need also to copy its JMSStockListDemoService.jar file under "JMSGateway/lib". It can be found under "JMS_StockList_Demo_Service/lib" from the [latest release](https://github.com/Weswit/Lightstreamer-jms-example-StockList-service-java/releases) of this project.
If you later plan to use object messages with your custom created objects, remember to also add their jar files under JMSGateway/lib.

Now open and configure JMSGateway/adapters.xml: you will find 4 sample data adapters configured for each of the 4 JMS brokers above. Find yours, enable it by changing the "disabled" param and set specific connection parameters (hostname, port, etc.).

Finally check the adapter log configuration file to specify logging level and destination.

## The Demo Services ##

Please copy the "JMS_StockList_Demo_Service" directory from the [latest release](https://github.com/Weswit/Lightstreamer-jms-example-StockList-service-java/releases) of this project anywhere in your file system, and put previously copied jars under its lib subdirectory.
Please copy here also the jms.jar obtained from Oracle's Java website.

Then configure the launch script start_stocklist_demo_service.bat (or start_stocklist_demo_service.sh if you are under Linux or Mac OS X), adding all the JMS broker-specific jars to the CUSTOM_JARS variable. It is preset with jars for HornetQ.

Now you should edit the configuration file. The included demo_service.conf file shows all available parameters and 4 sample configuration for the 4 JMS brokers above. Note that all parameters are required.

Finally check the log4j configuration file.

# See Also #

## Clients using this Service ##
* [Lightstreamer JMS Gateway StockList Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-JMS-example-StockList-client-javascript)

## Related projects ##
* [Lightstreamer StockList Demo Adapter](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java)
* [Lightstreamer Reusable Metadata Adapter in Java](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer JMS Gateway Portfolio Demo Service for Java](https://github.com/Weswit/Lightstreamer-JMS-example-Portfolio-service-java)
* [Lightstreamer JMS Gateway Chat Demo Service for Java](https://github.com/Weswit/Lightstreamer-JMS-example-Chat-service-java)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1
- Compatible with Lightstreamer JMS Gateway Adapter since version 1.0 or newer.