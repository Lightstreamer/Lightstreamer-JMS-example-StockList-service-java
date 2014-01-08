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

public class SubscribedItem {
	
    /**
     * This flag indicates whether the snapshot has already been sent
     * (on start it is obviously false).
     */
    public volatile boolean isSnapshotSent = false;

    /**
     * Represents the key for the itemHandle object
     * related to this item inside the handles map.
     */
    public String handleId;

    /**
     * The item name.
     */
    public String itemName;

    public SubscribedItem(String itemName, String handleId) {
        this.itemName = itemName;
        this.handleId = handleId;
    }
}