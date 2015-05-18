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

public class ListEvent implements ModelEvent {

	public enum Type {
		ADDED, REMOVED
	}

	private Type type;

	private Object parent;

	private Object list;

	private Object element;

	private int index;

	public ListEvent(Type type, Object parent, Object list, Object element,
			int index) {
		this.type = type;
		this.parent = parent;
		this.list = list;
		this.element = element;
		this.index = index;
	}

	public Type getType() {
		return type;
	}

	public Object getElement() {
		return element;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * @return the parent of the list. Could be {@code null}
	 */
	public Object getParent() {
		return parent;
	}

	@Override
	public Object getTarget() {
		return list;
	}
}
