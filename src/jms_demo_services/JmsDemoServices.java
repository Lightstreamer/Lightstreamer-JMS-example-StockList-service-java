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

package jms_demo_services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jms_demo_services.portfolio.PortfolioService;
import jms_demo_services.simple_chat.SimpleChatService;
import jms_demo_services.stock_list.StockListService;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class JmsDemoServices {
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
	                __log = Logger.getLogger(JmsDemoServices.class);
	                
	            } catch (Exception ex) {
	                ex.printStackTrace();
	                System.out.println(ex.getMessage());
	                throw new IllegalStateException(ERROR_LOG_CONF);
	            }
	
	        } else {
	            System.out.println(ERROR_LOG_CONF);
	            throw new IllegalStateException(ERROR_LOG_CONF);
	        }
	
	        __log.info("Demo services starting. Loading configuration...");
	        
	        // Read parameters
	        String jmsUrl= getParam(params, "jmsUrl", true, null);
            String initialContextFactory= getParam(params, "initialContextFactory", true, null);
            String connectionFactory= getParam(params, "connectionFactory", true, null);
            String stocksTopicName= getParam(params, "stocksTopicName", true, null);
            String stocksQueueName= getParam(params, "stocksQueueName", true, null);
            String portfolioTopicName= getParam(params, "portfolioTopicName", true, null);
            String portfolioQueueName= getParam(params, "portfolioQueueName", true, null);
            String chatTopicName= getParam(params, "chatTopicName", true, null);
            String chatQueueName= getParam(params, "chatQueueName", true, null);
            
            // Check for stocks only-mode
            boolean stocksOnly= false;
            if (args.length > 1) {
            	if (args[1].equals("-stocksOnly"))
            		stocksOnly= true;
            }
	
	        // Create our services passing read parameters
            new StockListService(__log, jmsUrl, initialContextFactory, connectionFactory, stocksTopicName, stocksQueueName);
            
            if (!stocksOnly) {
            	new PortfolioService(__log, jmsUrl, initialContextFactory, connectionFactory, portfolioTopicName, portfolioQueueName);
            	new SimpleChatService(__log, jmsUrl, initialContextFactory, connectionFactory, chatTopicName, chatQueueName);
            }
	
	        __log.info("Demo services ready.");
	      
    	} catch (Exception e) {
    		if (__log != null)
    			__log.error("Exception caught while starting Demo services: " + e.getMessage(), e);
    		
    		e.printStackTrace();
    	}
    }
}
