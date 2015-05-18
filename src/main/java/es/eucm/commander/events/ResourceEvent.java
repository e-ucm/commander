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
 * A resource has been added to/removed from the model
 */
public class ResourceEvent implements ModelEvent {

	public enum Type {
		ADDED, REMOVED
	}

	private Type type;

	private String id;

	private Object resource;

	private String category;

	public ResourceEvent(Type type, String id, Object resource, String category) {
		this.type = type;
		this.id = id;
		this.resource = resource;
		this.category = category;
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public Object getResource() {
		return resource;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public Object getTarget() {
		return null;
	}
}
