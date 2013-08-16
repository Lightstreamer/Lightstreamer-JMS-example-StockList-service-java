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