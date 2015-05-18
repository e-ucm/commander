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
package es.eucm.commander.events;

public class MapEvent implements ModelEvent {

	public enum Type {
		VALUE_CHANGED, ENTRY_ADDED, ENTRY_REMOVED
	}

	private Type type;

	private Object parent;

	private Object map;

	private Object key;

	private Object value;

	public MapEvent(Type type, Object parent, Object map, Object key,
			Object value) {
		this.type = type;
		this.parent = parent;
		this.map = map;
		this.key = key;
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public Object getParent() {
		return parent;
	}

	public Object getMap() {
		return map;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public Object getTarget() {
		return map;
	}
}
