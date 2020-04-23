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

package jms_demo_services.stock_list;

import java.util.Map;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms_demo_services.Configuration;
import jms_demo_services.JmsWrapper;


public class StockListService implements FeedListener {

  private static Logger log = LoggerFactory.getLogger(StockListService.class);

  /**
   * This object handles communications with JMS topic for feed publishing.
   */
  private final JmsWrapper stocksTopicWrapper;

  /**
   * This is the simulator for the classic StockListDemo.
   */
  private FeedSimulator feedSimulator;

  public StockListService(Configuration config) {

    // Instantiate a JMSWrapper for stocks topic
    stocksTopicWrapper = new JmsWrapper(config);

    // Instantiate and start the simulator. This is the object that "produce" data
    feedSimulator = new FeedSimulator();
    feedSimulator.setFeedListener(this);
  }

  public void start() {
    // Start the simulator
    feedSimulator.start();

    log.debug("Stock List service ready");
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
      stocksTopicWrapper.sendObjectMessage(toSend);

    } catch (JMSException je) {
      je.printStackTrace();
      log.error("Stock List: unable to send message - JMSException:" + je.getMessage());
    }
  }
}
