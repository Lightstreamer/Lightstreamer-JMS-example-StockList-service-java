/*
 * Copyright 2014 Weswit Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *

 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *limitations under the License.
 */

package jms_demo_services;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * This object can handle:
 * 1 MessageConsumer and
 * 1 MessageProducer 
 * related to the same destination
 * Session and Connection are shared between instances.
 */
public class JmsWrapper {
    private static Logger __log;

    private static String __initialContextFactory;
    private static String __providerURL;
    private static String __connectionFactoryName;

    private static Context __jndiContext;
    private static Connection __connection;

    private static boolean __jmsReady;

    private Session _session;
    private String _destinationName;
    private boolean _destinationIsTopic;
    private Destination _destination;
    private MessageProducer _producer;
    private MessageConsumer _consumer;

    private MessageListener _messageListener;
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Shared session and connection management
    
    public static void init(Logger logger, String initialContextFactory, String providerURL, String connectionFactoryName) {
        __log = logger;

        __initialContextFactory = initialContextFactory;
        __providerURL = providerURL;
        __connectionFactoryName = connectionFactoryName;
    }

    /**
     * Initiates the InitialContext.
     */
    private static void initJMS() throws JMSException, NamingException {
    	synchronized (JmsWrapper.class) {
	        if (__jmsReady)
	            return;
	
	        // Prepare a Properties object to be passed to the InitialContext
	        // constructor giving the InitialContextFactory name and
	        // the JMS server url
	        Properties properties = new Properties();
	        properties.put(Context.INITIAL_CONTEXT_FACTORY, __initialContextFactory);
	        properties.put(Context.PROVIDER_URL, __providerURL);
	
	        __jndiContext = new InitialContext(properties);
	        __log.info("JNDI Context[" + __jndiContext.getEnvironment() + "]...");
	
	        __jmsReady = true;
    	}
    }

    /**
     * Prepares the Session and the Connection.
     */
    private void initSession() throws JMSException, NamingException {
        if (_session != null)
            return;

        synchronized (JmsWrapper.class) {
        	if (__connection == null) {

	        	// First of all we have to inititiate the InitialContext
	        	// (without this we can't instantiate a Session)
	        	initJMS();
		        	
		        // Lookup to find our ConnectionFactory
		        __log.info("Looking up queue connection factory [" + __connectionFactoryName + "]...");
		        ConnectionFactory connectionFactory = (ConnectionFactory) __jndiContext.lookup(__connectionFactoryName);
		
		        // Get the Connection from our ConnectionFactory
		        __connection = connectionFactory.createConnection();
		        __log.debug("Connection created");
		        
		        // Start listening to JMS
		        __connection.start();
        	}
        	
            // Get the Session from our Connection
            _session = __connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            __log.debug("Session created");
            
            __log.debug("Connection started");
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Intance-bound consumer and producer management

    public JmsWrapper(String destinationName, boolean destinationIsTopic) {
        _destinationName = destinationName;
        _destinationIsTopic= destinationIsTopic;
    }

    public void setMessageListener(MessageListener messageListener) {
        _messageListener = messageListener;
    }

    /**
     * Prepares the MessageConsumer.
     */
    public synchronized void initConsumer() throws JMSException, NamingException {

        // First of all we have to inititiate the Session
        // (without this we can't instantiate a MessageConsumer)
    	initSession();

    	if (_destination == null) {

    		// Find our destination
	        __log.info("Looking up destination [" + _destinationName + "]...");
	        if (_destinationIsTopic)
	        	_destination = _session.createTopic(_destinationName);
	        else
	        	_destination = _session.createQueue(_destinationName);
    	}

        // Get the MessageConsumer from our Session
        _consumer = _session.createConsumer(_destination);
        __log.debug("Message consumer created");

        // If set we pass our MessageListener to the MessageConsumer
        if (_messageListener != null)
        	_consumer.setMessageListener(_messageListener);
    }

    /**
     * Prepares the MessageProducer.
     */
    public synchronized void initProducer() throws JMSException, NamingException {
    	
        // First of all we have to inititiate the Session
        // (without this we can't instantiate a MessageProducer)
        initSession();
        
    	if (_destination == null) {

    		// Find our destination
	        __log.info("Looking up destination [" + _destinationName + "]...");
	        if (_destinationIsTopic)
	        	_destination = _session.createTopic(_destinationName);
	        else
	        	_destination = _session.createQueue(_destinationName);
    	}

        // Get the MessageProducer from our Session
        _producer = _session.createProducer(_destination);
        __log.debug("Message producer created");
    }

    /**
     * Sends a text message.
     */
    public synchronized void sendTextMessage(String text) throws JMSException {
        TextMessage textMessage = null;

        synchronized (JmsWrapper.class) {

        	// Check if Session is ready
	        if (_session == null)
	            throw new JMSException("Session not ready");
	
	        // Get a message
	       	textMessage = _session.createTextMessage();
        }
        
        // Fill it with text (our message to be sent)
       	textMessage.setText(text);

        // Check if MessageProducer is ready
        if (_producer == null)
            throw new JMSException("Message producer not not ready");

        __log.debug("Sending message: " + text);

        // Send to JMS
       	_producer.send(textMessage);
    }
    
    /**
     * Sends an object message.
     */
    public synchronized void sendObjectMessage(Serializable obj) throws JMSException {
    	ObjectMessage objMessage= null;
    	
    	synchronized (JmsWrapper.class) {
    		
        	// Check if Session is ready
	        if (_session == null)
	            throw new JMSException("Session not not ready");
	
	        // Get a message
	        objMessage = _session.createObjectMessage();
    	}
    	
        // Fill it with obj (our message to be sent)
        objMessage.setObject(obj);

        // Check if MessageProducer is ready
        if (_producer == null)
            throw new JMSException("Message producer not not ready");

        __log.debug("Sending message object " + obj);

        // Send to JMS
       	_producer.send(objMessage);
    }
}
