/*
 * Copyright (c) Lightstreamer Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jms_demo_services.stocklist;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms_demo_services.config.Configuration;

/**
 * This object can handle: 1 MessageConsumer and 1 MessageProducer related to the same destination
 * Session and Connection are shared between instances.
 */
class JmsWrapper {
  private static Logger log = LoggerFactory.getLogger(JmsWrapper.class);

  private final Configuration config;

  private final Context jndiContext;

  private Connection connection;

  private Session session;

  private MessageProducer producer;

  JmsWrapper(Configuration config) {
    this.config = config;

    // Prepare a Properties object to be passed to the InitialContext
    // constructor giving the InitialContextFactory name and the JMS broker url
    Properties properties = new Properties();
    properties.put(Context.INITIAL_CONTEXT_FACTORY, config.initialContextFactory);
    properties.put(Context.PROVIDER_URL, config.jmsUrl);

    try {
      jndiContext = new InitialContext(properties);
      log.info("JNDI Context[{}]...", jndiContext.getEnvironment());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    initProducer();
  }

  /**
   * Prepares the Session and the Connection.
   */
  private void initSession() throws JMSException, NamingException {
    // Lookup to find our ConnectionFactory
    log.info("Looking up queue connection factory [{}] ...", config.connectionFactoryName);
    ConnectionFactory connectionFactory =
        (ConnectionFactory) jndiContext.lookup(config.connectionFactoryName);

    // Get the Connection from our ConnectionFactory
    if (config.username != null && config.password != null) {
      connection = connectionFactory.createConnection(config.username, config.password);
    } else {
      connection = connectionFactory.createConnection();
    }

    log.debug("Connection created");

    // Start listening to JMS
    connection.start();
    log.debug("Connection started");

    // Get the Session from our Connection
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    log.debug("Session created");
  }


  /**
   * Prepares the MessageProducer.
   */
  private void initProducer() {
    try {
      // First of all we have to initiate the Session
      // (without this we can't instantiate a MessageProducer)
      initSession();

      // Find our destination
      log.info("Looking up destination [{}]...", config.topicName);

      Destination destination;
      try {
        destination = (Destination) jndiContext.lookup(config.topicName);
      } catch (NamingException je) {
        // In case of dynamic destinations
        destination = session.createTopic(config.topicName);
      }

      // Get the MessageProducer from our Session
      producer = session.createProducer(destination);
      log.debug("Message producer created");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sends an object message.
   */
  public synchronized void sendObjectMessage(Serializable obj) throws JMSException {
    // Get a message
    ObjectMessage objMessage = session.createObjectMessage();

    // Fill it with object (our message to be sent)
    objMessage.setObject(obj);

    log.debug("Sending message object " + obj);

    // Send to JMS
    producer.send(objMessage);
  }
}
