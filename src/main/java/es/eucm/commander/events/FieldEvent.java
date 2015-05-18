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
 * Event produced when a field is modified
 */
public class FieldEvent implements ModelEvent {

	private String field;

	private Object target;

	private Object value;

	/**
	 * @param target
	 *            object whos field was modified
	 * @param field
	 *            field name
	 * @param value
	 *            new value for the field
	 */
	public FieldEvent(Object target, String field, Object value) {
		this.field = field;
		this.target = target;
		this.value = value;
	}

	/**
	 * @return new value of the field
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return name of the field
	 */
	public String getField() {
		return field;
	}

	@Override
	public Object getTarget() {
		return target;
	}
}
