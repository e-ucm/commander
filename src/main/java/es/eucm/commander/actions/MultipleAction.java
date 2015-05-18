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
package es.eucm.commander.actions;

import es.eucm.commander.commands.Command;
import es.eucm.commander.commands.CompositeCommand;

/**
 * Action to invoke multiple actions and generate only 1
 * {@link CompositeCommand}. Implement {@link #performActions(Object...)}, and
 * call {@link #action(Class, Object...)} from there to accumulate in one
 * {@link CompositeCommand} all the actions executed.
 */
public abstract class MultipleAction extends Action {

	private CompositeCommand compositeCommand;

	public void action(Class clazz, Object... args) {
		Action action = commander.getAction(clazz);
		Command command = action.perform(args);
		if (command != null) {
			compositeCommand.addCommand(command);
		}
	}

	@Override
	public CompositeCommand perform(Object... args) {
		compositeCommand = new CompositeCommand();
		performActions(args);
		return compositeCommand;
	}

	public abstract void performActions(Object... args);

}
