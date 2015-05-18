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

import es.eucm.commander.commands.Command;
import es.eucm.commander.events.ModelEvent;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Implements the commands stack
 */
public class Commands {

	public enum Type {
		COMMAND, UNDO, REDO, POP, PUSH, CLEAR
	}

	private Model model;

	private ArrayList<CommandListener> listeners;

	private Stack<CommandsStack> stacks;

	private CommandsStack currentStack;

	/**
	 * 
	 * @param model
	 *            the game project model
	 */
	public Commands(Model model) {
		this.model = model;
		listeners = new ArrayList<CommandListener>();
		this.stacks = new Stack<CommandsStack>();
		pushStack();
	}

	/**
	 * Executes the command. This clears the redo history
	 * 
	 * @param command
	 *            the command
	 */
	public void command(Command command) {
		if (currentStack != null) {
			currentStack.command(command);
		} else {
			doCommand(command);
		}
	}

	/**
	 * Undoes the last command
	 */
	public void undo() {
		if (currentStack != null) {
			currentStack.undo();
		}
	}

	/**
	 * Executes the last undone command, if any
	 */
	public void redo() {
		if (currentStack != null) {
			currentStack.redo();
		}
	}

	/**
	 * @return the current undo history. Could be null if there is no current
	 *         command stack
	 */
	public Stack<Command> getUndoHistory() {
		return currentStack == null ? null : currentStack.getUndoHistory();
	}

	/**
	 * @return the current redo history. Could be null if there is no current
	 *         command stack
	 */
	public Stack<Command> getRedoHistory() {
		return currentStack == null ? null : currentStack.getRedoHistory();
	}

	public void doCommand(Command command) {
		ModelEvent modelEvent = command.doCommand();
		model.notify(modelEvent);
	}

	/**
	 * Creates a new context with an independent commands stack with infinite
	 * state. Previous commands received won't be able to be undone until
	 * {@link #popStack(boolean)} is called.
	 */
	public void pushStack() {
		pushStack(-1);
	}

	/**
	 * Creates a new context with an independent commands stack. Previous
	 * commands received won't be able to undone until
	 * {@link #popStack(boolean)} is called.
	 * 
	 * @param maxCommands
	 *            maximum number of commands the stack can contain
	 */
	public void pushStack(int maxCommands) {
		currentStack = new CommandsStack(maxCommands);
		stacks.push(currentStack);
		fire(Type.PUSH, null);
	}

	/**
	 * Exits the current commands stack, returning to the previous one.
	 * 
	 * @param merge
	 *            if commands of the context left behind must be added at the
	 *            end of the previous commands stack
	 */
	public void popStack(boolean merge) {
		CommandsStack oldCommandsStack = stacks.pop();
		if (!stacks.isEmpty()) {
			currentStack = stacks.peek();
			if (merge) {
				currentStack.getUndoHistory().addAll(
						oldCommandsStack.getUndoHistory());
			}
		} else {
			currentStack = null;
		}
		fire(Type.POP, null);
	}

	public Stack<CommandsStack> getCommandsStack() {
		return stacks;
	}

	public void addListener(CommandListener commandListener) {
		listeners.add(commandListener);
	}

	public void removeListener(CommandListener commandListener) {
		listeners.remove(commandListener);
	}

	/**
	 * Clears all the commands stack
	 */
	public void clear() {
		if (currentStack != null) {
			currentStack = null;
		}
		stacks.clear();
		fire(Type.CLEAR, null);
	}

	private void fire(Type type, Command command) {
		for (CommandListener listener : listeners) {
			listener.updated(this, type, command);
		}
	}

	public class CommandsStack {

		private int maxCommands;

		private Stack<Command> undoHistory;

		private Stack<Command> redoHistory;

		public CommandsStack(int maxCommands) {
			this.maxCommands = maxCommands;
			undoHistory = new Stack<Command>();
			redoHistory = new Stack<Command>();
		}

		public Stack<Command> getUndoHistory() {
			return undoHistory;
		}

		public Stack<Command> getRedoHistory() {
			return redoHistory;
		}

		/**
		 * Executes the command. Clears the redo history
		 */
		public void command(Command command) {
			if (redoHistory.isEmpty() || !command.isTransparent()) {
				redoHistory.clear();
				if (command.canUndo()) {
					if (undoHistory.isEmpty()
							|| (!undoHistory.peek().combine(command))) {
						if (maxCommands != -1 && !undoHistory.isEmpty()
								&& undoHistory.size() >= maxCommands) {
							undoHistory.remove(0);
						}
						undoHistory.add(command);
					}
				}
			}
			doCommand(command);
			fire(Type.COMMAND, command);
		}

		/**
		 * Undo last command, if any
		 */
		public void undo() {
			if (!undoHistory.isEmpty()) {
				Command command;
				do {
					command = undoHistory.pop();
					redoHistory.add(command);
					model.notify(command.undoCommand());
					fire(Type.UNDO, command);
				} while (command.isTransparent() && !undoHistory.isEmpty());
			}
		}

		/**
		 * Redo last command, if any
		 */
		public void redo() {
			if (!redoHistory.isEmpty()) {
				Command command = redoHistory.pop();
				undoHistory.add(command);
				doCommand(command);
				fire(Type.REDO, command);

				while (!redoHistory.isEmpty()
						&& redoHistory.peek().isTransparent()) {
					command = redoHistory.pop();
					undoHistory.add(command);
					doCommand(command);
					fire(Type.REDO, command);
				}
			}
		}
	}

	public interface CommandListener {

		/**
		 * A command is executed
		 * 
		 * @param commands
		 *            the commands object
		 * @param type
		 *            type of update
		 * @param command
		 *            the command executed, in some types, can be null
		 */
		void updated(Commands commands, Type type, Command command);
	}

}
