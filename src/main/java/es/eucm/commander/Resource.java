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
package es.eucm.commander;

/**
 * A resource in the model.
 */
public class Resource {

	public enum State {
		COMMITTED, MODIFIED, REMOVED
	}

	private String category;

	private String id;

	private Object object;

	private State state;

	/**
	 * Creates a modified resource.
	 */
	public Resource(String category, String id, Object object) {
		this(category, id, object, State.MODIFIED);
	}

	public Resource(String category, String id, Object object, State state) {
		this.id = id;
		this.category = category;
		this.object = object;
		this.state = state;
	}

	public String getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	public Object getObject() {
		return object;
	}

	public State getState() {
		return state;
	}

	public void modify() {
		if (this.state == State.REMOVED) {
			throw new RuntimeException(
					"A removed resource cannot be marked as modified");
		}
		this.state = State.MODIFIED;
	}

	public void commit() {
		if (this.state == State.REMOVED) {
			throw new RuntimeException(
					"A removed resource cannot be marked as commited");
		}
		this.state = State.COMMITTED;
	}

	public void remove() {
		this.state = State.REMOVED;
	}

}
