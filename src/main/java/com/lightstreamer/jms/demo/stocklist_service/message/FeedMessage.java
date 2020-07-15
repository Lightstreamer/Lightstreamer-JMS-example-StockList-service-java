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

package com.lightstreamer.jms.demo.stocklist_service.message;

import java.io.Serializable;
import java.util.Map;

/** A message published by StockList service and received from client. */
public class FeedMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  // The item name
  public String itemName;

  // An HashMap containing the updates for the item (the field names are the keys)
  public Map<String, String> currentValues;

  public FeedMessage() {}

  public FeedMessage(String itemName, final Map<String, String> currentValues) {
    this.itemName = itemName;
    this.currentValues = currentValues;
  }
}
