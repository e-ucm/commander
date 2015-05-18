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

import es.eucm.commander.events.FieldEvent;

import java.lang.reflect.Field;

/**
 * A command that changes a field-value. The most common case of command.
 */
public class FieldCommand extends Command {

	protected Object oldValue;

	protected Object newValue;

	private String fieldName;

	private Object target;

	private boolean combine;

	private Field field;

	public FieldCommand(Object target, String fieldName, Object newValue) {
		this(target, fieldName, newValue, false);
	}

	/**
	 * 
	 * @param newValue
	 *            new value (T)
	 * @param target
	 *            where the value should be set
	 * @param fieldName
	 *            name of writable attribute in target
	 */
	public FieldCommand(Object target, String fieldName, Object newValue,
			boolean combine) {
		this.newValue = newValue;
		this.fieldName = fieldName;
		this.target = target;
		this.combine = combine;
		this.field = getField(target, fieldName);
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public FieldEvent doCommand() {
		if (field == null) {
			return null;
		}

		try {
			oldValue = field.get(target);
		} catch (IllegalAccessException e) {
			return null;
		}

		return setValue(newValue);
	}

	/**
	 * Sets the value and returns a mode-event describing what nodes have
	 * changed. Called by both undo() and redo().
	 * 
	 * @param value
	 * @return
	 */
	protected FieldEvent setValue(Object value) {
		if (field == null) {
			return null;
		}

		try {
			field.set(target, value);
		} catch (Exception e) {
			return null;
		}

		return new FieldEvent(target, fieldName, value);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public FieldEvent undoCommand() {
		return setValue(oldValue);
	}

	@Override
	public boolean combine(Command command) {
		if (command instanceof FieldCommand) {
			FieldCommand o = (FieldCommand) command;
			if (this.combine && o.target == this.target
					&& o.fieldName.equals(this.fieldName)) {
				newValue = o.newValue;
				this.combine = o.combine;
				return true;
			}
		}
		return false;
	}

	private Field getField(Object target, String fieldName) {
		Field field = null;
		Class<?> clazz = target.getClass();
		while (clazz != null) {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			}
			clazz = clazz.getSuperclass();
		}
		if (field != null) {
			field.setAccessible(true);
		}
		return field;
	}

}
