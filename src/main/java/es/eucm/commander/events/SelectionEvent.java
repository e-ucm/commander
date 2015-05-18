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

/**
 * The current selection in the editor changed
 */
public class SelectionEvent implements ModelEvent {

	public enum Type {
		ADDED, REMOVED, FOCUSED
	}

	private Type type;

	private String parentContextId;

	private String contextId;

	private Object[] selection;

	public SelectionEvent(Type type, String parentContextId, String contextId,
			Object[] selection) {
		this.type = type;
		this.parentContextId = parentContextId;
		this.contextId = contextId;
		this.selection = selection;
	}

	public Type getType() {
		return type;
	}

	public String getParentContextId() {
		return parentContextId;
	}

	public String getContextId() {
		return contextId;
	}

	public Object[] getSelection() {
		return selection;
	}

	@Override
	public Object getTarget() {
		return null;
	}
}
