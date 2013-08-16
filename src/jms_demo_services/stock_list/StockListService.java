/*
 *
 * Copyright 2013 Weswit s.r.l.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jms_demo_services.stock_list;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import jms_demo_services.JmsWrapper;

import org.apache.log4j.Logger;

public class StockListService implements FeedListener, MessageListener {
    private static final String ERROR_MSG_NOT_COMPATIBLE = "message received was not compatible with this process. Maybe someone else sending messages? ";
    private static final String WARNING_SUBSCR_UNEXP_ITEM = "(subscribing) unexpected item: ";
    private static final String WARNING_UNSUBSCR_UNEXP_ITEM = "(unsubscribing) unexpected item: ";
    private static final String WARNING_UNSUBSCR_UNEXP_HANDLE = "(unsubscribing) unexpected handle for item: ";

    private Logger _log;

    /**
     * This object handles comunications with JMS queue for
     * feed subscriptions.
     */
    private JmsWrapper _stocksQueueWrapper;
    
    /**
     * This object handles comunications with JMS topic for
     * feed publishing.
     */
    private JmsWrapper _stocksTopicWrapper;

    /**
     * This Map contains info about the subscribed items.
     */
    private Map<String, SubscribedItem> _subscribedItems;

    /**
     * This is the simulator for the classic StockListDemo.
     */
    private FeedSimulator _feedSimulator;

    public StockListService(Logger log, String providerURL, String initialContextFactory, String connectionFactory, String topic, String queue) throws NamingException, JMSException {
    	_log= log;
    	
    	_subscribedItems = new HashMap<String, SubscribedItem>();
    	
    	// Initialize JMSWrapper shared components
    	JmsWrapper.init(_log, initialContextFactory, providerURL, connectionFactory);
    	
        // Instantiate a JMSWrapper for stocks queue
        _stocksQueueWrapper = new JmsWrapper(queue, false);
        
        // This service will be the JMS listener
        _stocksQueueWrapper.setMessageListener(this);

        // Instantiate a JMSWrapper for stocks topic
        _stocksTopicWrapper = new JmsWrapper(topic, true);

        // Instantiate and start the simulator. This is the object that "produce" data
        _feedSimulator = new FeedSimulator();
        _feedSimulator.setFeedListener(this);
        _feedSimulator.start();
        
        // Start JMS
        _stocksQueueWrapper.initConsumer();
        _stocksTopicWrapper.initProducer();

        _log.debug("Stock List service ready");
	}
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Message listener

    /**
     * Receive messages from JMSWrapper.
     */
    public void onMessage(Message message) {
        String feedMsg = null;
        
        _log.debug("Stock List: message received: processing...");
        
        try {

        	// Pull out text from the Message object
            TextMessage textMessage = (TextMessage) message;
            feedMsg = textMessage.getText();
            _log.debug("Stock List: message: TextMessage received: " + feedMsg);
        
        } catch (ClassCastException cce) {

        	// If message isn't a TextMessage then this update is not "correct"
            _log.warn("Stock List: " + ERROR_MSG_NOT_COMPATIBLE + "(ClassCastException)");
            return;
        
        } catch (JMSException jmse) {
            _log.error("Stock List: JMSException: " + jmse.getMessage());
            return;
        }

        String itemName = null;
        String handleId = null;
        if (feedMsg != null) {
            _log.debug("Stock List: received message: " + feedMsg);

            if (feedMsg.equals("reset")) {
                reset();
            
            } if (feedMsg.indexOf("subscribe") == 0) {
            	
                // This is a subscribe message
                itemName = feedMsg.substring(9,feedMsg.indexOf("_"));
                handleId = feedMsg.substring(feedMsg.indexOf("_")+1);
                subscribe(itemName,handleId);
            
            } else if (feedMsg.indexOf("unsubscribe") == 0) {
            	
                // This is a unsubscribe message
                itemName = feedMsg.substring(11,feedMsg.indexOf("_"));
                handleId = feedMsg.substring(feedMsg.indexOf("_")+1);
                unsubscribe(itemName,handleId);
            }
        }

        if (itemName == null) {
         
        	// The message isn't a valid message
            _log.warn("Stock List: " + ERROR_MSG_NOT_COMPATIBLE + "Message: " + feedMsg);
        }
    }

    private void reset() {
        synchronized (_subscribedItems) {
            _subscribedItems = new HashMap<String,SubscribedItem>();
        }
    }

    private void subscribe(String itemName, String handleId) {
        _log.debug("Stock List: subscribing " + itemName + "(" + handleId + ")");

        if (!itemName.startsWith("item")) {
            
        	// Item composed by "item" + ID: this is not a valid one
            _log.error("Stock List: " + WARNING_SUBSCR_UNEXP_ITEM + itemName + "(" + handleId + ")");
            return;
        }
        
        try {
            int nI = Integer.parseInt(itemName.substring(4));
            if (nI > 30 || nI < 1) {

            	// This item is not in the admitted range
                _log.error("Stock List: " + WARNING_SUBSCR_UNEXP_ITEM + itemName + "(" + handleId + ")");
                return;
            }
            
        } catch (NumberFormatException nfe) {
            
        	// Non-numeric itemID not admitted
            _log.error("Stock List: " + WARNING_SUBSCR_UNEXP_ITEM + itemName + "(" + handleId + ")");
            return;
        }

        _log.debug("Stock List: (subscribing) valid item: " + itemName + "(" + handleId + ")");

        synchronized (_subscribedItems) {
            
        	// Put the item in the subscribedItems map
            // if another subscription is already in that will be replaced
            SubscribedItem attr = new SubscribedItem(itemName,handleId);
            _subscribedItems.put(itemName, attr);
        }
        
        // Now we ask the feed for the snapshot; our feed will insert
        // an event with snapshot information into the normal updates flow
        _feedSimulator.sendCurrentValues(itemName);
        
        _log.info("Stock List: subscribed " + itemName + "(" + handleId + ")");
    }

    private void unsubscribe(String itemName, String handleId) {
        _log.debug("Stock List: unsubscribing " + itemName + "(" + handleId + ")");
        
        synchronized (_subscribedItems) {
            if (!_subscribedItems.containsKey(itemName)) {
        
            	// Here checks are useless, just try to get the item from the
                // subscribedItems map, if not contained there is an error
                _log.error("Stock List: " + WARNING_UNSUBSCR_UNEXP_ITEM + itemName + "(" + handleId + ")");
                return;
            }

            SubscribedItem sia = _subscribedItems.get(itemName);
            if (sia.handleId == handleId) {

            	// Remove the item from the subscribedItems map.
                _subscribedItems.remove(itemName);

            } else
                _log.warn("Stock List: " + WARNING_UNSUBSCR_UNEXP_HANDLE + itemName + "(" + handleId + ")");
        }

        _log.info("Stock List: unsubscribed " + itemName + "(" + handleId + ")");
    }


    ///////////////////////////////////////////////////////////////////////////
    // Feed listener

    /**
     * Receives update from the feed simulator.
     */
    public void onFeedUpdate(String itemName, Map<String, String> currentValues, boolean isSnapshot) {
        SubscribedItem sia = null;

        synchronized (_subscribedItems) {
            if (!_subscribedItems.containsKey(itemName)) {

            	// Simulator always produces all updates. Here we filter
                // non-subscribed items
                return;
            }

            // Handle the snapshot status
            sia = _subscribedItems.get(itemName);

            if (!sia.isSnapshotSent) {
                if (!isSnapshot) {

                	// We ignore the update and keep waiting until
                    // a full snapshot for the item has been received
                    return;
                }
                
                sia.isSnapshotSent = true;

            } else {
                if (isSnapshot) {
                    
                	// It's not the first event we have received carrying
                    // snapshot information for the item; so, this event
                    // is not a snapshot from Lightstreamer point of view
                    isSnapshot = false;
                }
            }
        }

        // Prepare the object to send through JMS
        FeedMessage toSend = new FeedMessage(itemName, currentValues, isSnapshot, sia. handleId);
        try {

        	// Publish the update to JMS
            _stocksTopicWrapper.sendObjectMessage(toSend);
            
        } catch (JMSException je) {
            _log.error("Stock List: unable to send message - JMSException:" + je.getMessage());
        }
    }
}
