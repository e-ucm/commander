/**
 * Copyright (C) 2015 e-UCM Research Group (e-adventure-dev@e-ucm.es)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.eucm.commander.commands;

/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */

import es.eucm.commander.events.MapEvent;
import es.eucm.commander.events.MapEvent.Type;

/**
 * Contains subclasses for adding to and removing from maps
 * 
 */
public abstract class MapCommand extends Command {

	protected Object parent;

	/**
	 * The map in which the elements will be placed.
	 */
	protected MapWrapper map;

	protected Object newKey;
	protected Object oldKey;
	protected Object newValue;
	protected Object oldValue;

	/**
	 * Constructor for the ChangeMap class.
	 */
	protected MapCommand(Object parent, MapWrapper map, Object key, Object value) {
		this.parent = parent;
		this.map = map;
		this.newKey = key;
		this.newValue = value;
	}

	@Override
	public MapEvent doCommand() {
		if (newValue == null) {
			// If no new value, remove
			oldKey = newKey;
			oldValue = map.remove(oldKey);
			return new MapEvent(Type.ENTRY_REMOVED, parent, map.getMap(),
					oldKey, oldValue);
		} else {
			// If new value, add or substitute
			oldValue = map.get(newKey);
			map.put(newKey, newValue);
			if (oldValue == null) {
				return new MapEvent(Type.ENTRY_ADDED, parent, map.getMap(),
						newKey, newValue);
			} else {
				return new MapEvent(Type.VALUE_CHANGED, parent, map.getMap(),
						newKey, newValue);
			}
		}
	}

	@Override
	public MapEvent undoCommand() {
		if (newValue == null) {
			// It was a remove
			map.put(oldKey, oldValue);
			return new MapEvent(Type.ENTRY_ADDED, parent, map.getMap(), oldKey,
					oldValue);
		} else {
			// It was a put
			if (oldValue == null) {
				// It was a new entry, remove
				map.remove(newKey);
				return new MapEvent(Type.ENTRY_REMOVED, parent, map.getMap(),
						newKey, newValue);
			} else {
				// It was a substitution, recover previous value
				map.put(newKey, oldValue);
				return new MapEvent(Type.VALUE_CHANGED, parent, map.getMap(),
						newKey, oldValue);
			}
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public boolean combine(Command command) {
		return false;
	}

	public static class PutToMapCommand extends MapCommand {

		/**
		 * Put a key-value in the map
		 * 
		 * @param map
		 *            the value
		 * @param key
		 *            the key
		 * @param value
		 *            the value
		 */
		public PutToMapCommand(Object parent, MapWrapper map, Object key,
				Object value) {
			super(parent, map, key, value);
		}
	}

	public static class RemoveFromMapCommand extends MapCommand {

		/**
		 * Removed an ent
		 * 
		 * @param map
		 *            the map
		 * @param key
		 *            the key from the entry to remove
		 */
		public RemoveFromMapCommand(Object parent, MapWrapper map, Object key) {
			super(parent, map, key, null);
		}
	}

	public interface MapWrapper {

		Object getMap();

		Object remove(Object key);

		void put(Object key, Object value);

		Object get(Object newKey);
	}

}
