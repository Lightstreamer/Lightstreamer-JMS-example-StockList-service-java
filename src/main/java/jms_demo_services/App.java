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

package jms_demo_services;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jms_demo_services.config.Configuration;
import jms_demo_services.stocklist.StockListService;

public class App {

  private static Logger log = LoggerFactory.getLogger(App.class);

  /**
   * Main method. Called by script.
   */
  public static void main(String[] args) {
    try (InputStream is = App.class.getResourceAsStream("/service.conf")) {
      log.info("StockList Demo service starting. Loading configuration...");

      Properties props = new Properties();
      props.load(is);

      // Read parameters
      //@formatter:off
      Configuration config =  new Configuration.Builder()
        .withJmsURL(props.getProperty("jmsUrl"))
        .withInitialiContextFactory(props.getProperty("initialContextFactory"))
        .withConnectionFactoryName(props.getProperty("connectionFactoryName"))
        .withTopic(props.getProperty("topic"))
        .withCredentials(props.getProperty("user"), props.getProperty("password"))
        .build();
      //@formatter:on

      // Create and start our service passing the supplied configuration
      new StockListService(config).start();

      log.info("StockList Demo service ready.");
    } catch (Exception e) {
      if (log != null) {
        log.error("Exception caught while starting StockList Demo service: " + e.getMessage(), e);
      }
    }
  }
}
