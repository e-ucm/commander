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

import es.eucm.commander.Commander;
import es.eucm.commander.Commands;
import es.eucm.commander.Commands.CommandListener;
import es.eucm.commander.Commands.Type;
import es.eucm.commander.commands.Command;

/**
 * Undoes the last action
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * This action receives no arguments.
 * </dl>
 */
public class Undo extends Action implements CommandListener {

	private Commands commands;

	@Override
	public void addedToActions(Commander commander) {
		commands = commander.getCommands();
		updateEnable();
		commands.addListener(this);
	}

	@Override
	public Command perform(Object... args) {
		commands.undo();
		return null;
	}

	private void updateEnable() {
		setEnabled(!commands.getUndoHistory().empty());
	}

	@Override
	public void updated(Commands commands, Type type, Command command) {
		updateEnable();
	}
}
