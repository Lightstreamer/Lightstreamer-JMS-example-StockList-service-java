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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jms_demo_services.stock_list.StockListService;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class JmsStockListDemoService {
    private static Logger __log;

    private static String ERROR_NO_CONF = "Please specify a valid configuration file as parameter.\nProcess exits.\n";
    private static String ERROR_NO_PARAM = " is missing.\nProcess exits";
    private static String WARNING_PARAM_DEFAULT = " is missing. Using default.";
    private static String ERROR_LOG_CONF = "Log configuration fails. Check you configuration files\nProcess exits";
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Utils

    private static String getParam (Properties params, String toGet, boolean required, String def) {
        String res = params.getProperty(toGet);
        if (res == null) {
            if (required) {
                throw new IllegalStateException(toGet + ERROR_NO_PARAM);
            } else {
                if (__log != null) {
                    __log.warn(toGet + WARNING_PARAM_DEFAULT);
                }
                res = def;
            }
        }

        if (__log != null) {
            __log.debug(toGet+ ": " + res);
        }

        return res;
    }

    
    ///////////////////////////////////////////////////////////////////////////
    // Main
    
    /**
     * Main method. Called by script.
     */
    public static void main(String[] args) {
    	try {
	        if (args == null || args.length < 1 || args[0] == null) {
	        	
	            // If no arguments passed we exit. We need 1 parameter
	            // containing the path of the configuration file
	            System.out.println(ERROR_NO_CONF);
	            return;
	        }
	
	        // Read configuration file
	        File configFile = new File(args[0]);
	        Properties params = new Properties();
	        try {
	            params.load(new FileInputStream(configFile));
	
	        } catch (FileNotFoundException e) {
	            System.out.println(ERROR_NO_CONF);
	            return;
	        
	        } catch (IOException e) {
	            System.out.println(ERROR_NO_CONF);
	            return;
	        }
	
	        // Configure a log4j logger
	        String logConf= getParam(params,"logConf", true, null);
	        if (logConf != null) {
	            try {
	                DOMConfigurator.configureAndWatch(logConf, 10000);
	                __log = Logger.getLogger(JmsStockListDemoService.class);
	                
	            } catch (Exception ex) {
	                ex.printStackTrace();
	                System.out.println(ex.getMessage());
	                throw new IllegalStateException(ERROR_LOG_CONF);
	            }
	
	        } else {
	            System.out.println(ERROR_LOG_CONF);
	            throw new IllegalStateException(ERROR_LOG_CONF);
	        }
	
	        __log.info("StockList Demo service starting. Loading configuration...");
	        
	        // Read parameters
	        String jmsUrl= getParam(params, "jmsUrl", true, null);
            String initialContextFactory= getParam(params, "initialContextFactory", true, null);
            String connectionFactory= getParam(params, "connectionFactory", true, null);
            String stocksTopicName= getParam(params, "stocksTopicName", true, null);
            String stocksQueueName= getParam(params, "stocksQueueName", true, null);
            
	        // Create our service passing read parameters
            new StockListService(__log, jmsUrl, initialContextFactory, connectionFactory, stocksTopicName, stocksQueueName);
	
	        __log.info("StockList Demo service ready.");
	        
	        // Avoid termination
	        Object semaphore= new Object();
	        synchronized (semaphore) {
	        	semaphore.wait();
	        }
	      
    	} catch (Exception e) {
    		if (__log != null)
    			__log.error("Exception caught while starting StockList Demo service: " + e.getMessage(), e);
    		
    		e.printStackTrace();
    	}
    }
}
