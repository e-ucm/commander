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

import java.util.ArrayList;

/**
 * Convenient class for grouping commands that need to be always undone and
 * redone together
 */
public class CompositeCommand extends Command {

	protected ArrayList<Command> commands;

	/**
	 * Creates a Composite Command with an arbitrary number of commands that
	 * will be executed in order.
	 * 
	 * @param commands
	 *            The list of commands to execute in order.
	 */
	public CompositeCommand(Command... commands) {
		this.commands = new ArrayList<Command>();
		for (Command command : commands) {
			addCommand(command);
		}
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}

	public void addCommand(Command command) {
		commands.add(command);
	}

	public void addAll(ArrayList<Command> commands) {
		for (Command command : commands) {
			addCommand(command);
		}
	}

	@Override
	public MultipleEvent doCommand() {
		MultipleEvent multipleEvent = new MultipleEvent();
		for (Command command : commands) {
			multipleEvent.addEvent(command.doCommand());
		}
		return multipleEvent;
	}

	@Override
	public MultipleEvent undoCommand() {
		MultipleEvent multipleEvent = new MultipleEvent();
		for (int i = commands.size() - 1; i >= 0; i--) {
			multipleEvent.addEvent(commands.get(i).undoCommand());
		}
		return multipleEvent;
	}

	@Override
	public boolean canUndo() {
		for (Command c : commands) {
			if (!c.canUndo())
				return false;
		}
		return true;
	}

	@Override
	public boolean isTransparent() {
		for (Command command : commands) {
			if (!command.isTransparent()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean combine(Command command) {
		return false;
	}
}
