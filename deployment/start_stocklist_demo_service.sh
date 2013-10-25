#! /bin/sh

CUSTOM_JARS=./lib/hornetq-commons.jar:./lib/hornetq-core-client.jar:./lib/hornetq-jms-client.jar:./lib/jnp-client.jar:./lib/netty.jar

cpath=./lib/JMSStockListDemoService.jar:./lib/log4j-1.2.15.jar:./lib/jms.jar:$CUSTOM_JARS
class=jms_demo_services.JmsStockListDemoService

command="java -cp $cpath $class conf/demo_service.conf"

exec $command

