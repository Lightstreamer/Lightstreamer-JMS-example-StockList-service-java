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

import java.io.Serializable;
import java.util.Map;

/**
 * A message published by StockList service and received from client.
 */
public class FeedMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // The item name
    public String itemName = null;

    // An HashMap containing the updates for the item (the field names are the keys)
    public Map<String, String> currentValues = null;

    // Indicate if the map carries the entire snapshot for the item
    public boolean isSnapshot = false;

    // The id related to the handle of this item
    public String handleId = null;
    
    public FeedMessage() {}

    public FeedMessage(String itemName, final Map<String, String> currentValues, boolean isSnapshot, String handleId) {
        this.itemName = itemName;
        this.currentValues = currentValues;
        this.isSnapshot = isSnapshot;
        this.handleId = handleId;
    }
}