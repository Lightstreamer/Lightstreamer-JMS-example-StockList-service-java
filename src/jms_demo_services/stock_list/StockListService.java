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

package jms_demo_services.stock_list;

import java.util.Map;

import javax.jms.JMSException;
import javax.naming.NamingException;

import jms_demo_services.JmsWrapper;

import org.apache.log4j.Logger;

public class StockListService implements FeedListener {

    private Logger _log;

    /**
     * This object handles comunications with JMS topic for
     * feed publishing.
     */
    private JmsWrapper _stocksTopicWrapper;

    /**
     * This is the simulator for the classic StockListDemo.
     */
    private FeedSimulator _feedSimulator;

    public StockListService(Logger log, String providerURL, String initialContextFactory, String connectionFactory, String topic) throws NamingException, JMSException {
    	_log= log;
    	
    	// Initialize JMSWrapper shared components
    	JmsWrapper.init(_log, initialContextFactory, providerURL, connectionFactory);
    	
        // Instantiate a JMSWrapper for stocks topic
        _stocksTopicWrapper = new JmsWrapper(topic, true);

        // Instantiate and start the simulator. This is the object that "produce" data
        _feedSimulator = new FeedSimulator();
        _feedSimulator.setFeedListener(this);
        _feedSimulator.start();
        
        // Start JMS
        _stocksTopicWrapper.initProducer();

        _log.debug("Stock List service ready");
	}

    ///////////////////////////////////////////////////////////////////////////
    // Feed listener

    /**
     * Receives update from the feed simulator.
     */
    public void onFeedUpdate(String itemName, Map<String, String> currentValues, boolean isSnapshot) {
        // Prepare the object to send through JMS
        FeedMessage toSend = new FeedMessage(itemName, currentValues);
        try {

        	// Publish the update to JMS
            _stocksTopicWrapper.sendObjectMessage(toSend);
            
        } catch (JMSException je) {
            _log.error("Stock List: unable to send message - JMSException:" + je.getMessage());
        }
    }
}
