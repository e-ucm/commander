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

import es.eucm.commander.events.MultipleEvent;

public class MultipleFieldsCommand extends CompositeCommand {

	private Object target;

	private boolean combine;

	public MultipleFieldsCommand(Object target, boolean combine) {
		this.target = target;
		this.combine = combine;
	}

	public MultipleFieldsCommand field(String fieldName, Object value) {
		commands.add(new FieldCommand(target, fieldName, value, true));
		return this;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public MultipleEvent undoCommand() {
		MultipleEvent event = new MultipleEvent();
		// Undo commands in inverse order
		for (int i = commands.size() - 1; i >= 0; i--) {
			event.addEvent(commands.get(i).undoCommand());
		}
		return event;
	}

	@Override
	public boolean combine(Command other) {
		if (combine && other instanceof MultipleFieldsCommand) {
			MultipleFieldsCommand c = (MultipleFieldsCommand) other;
			if (c.target == this.target
					&& c.commands.size() == this.commands.size()) {
				// Check if they can be combined
				for (int i = 0; i < commands.size(); i++) {
					FieldCommand c1 = (FieldCommand) commands.get(i);
					FieldCommand c2 = (FieldCommand) c.commands.get(i);
					if (!c1.getFieldName().equals(c2.getFieldName())) {
						return false;
					}
				}
				// Now, combine
				for (int i = 0; i < commands.size(); i++) {
					FieldCommand c1 = (FieldCommand) commands.get(i);
					FieldCommand c2 = (FieldCommand) c.commands.get(i);
					c1.combine(c2);
				}
				return true;
			}
		}
		return false;
	}
}
