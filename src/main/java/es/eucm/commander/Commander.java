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

import es.eucm.commander.actions.Action;
import es.eucm.commander.actions.Action.ActionListener;
import es.eucm.commander.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Takes care of the actions execution
 */
public class Commander {

	private Commands commands;

	private Model model;

	private Selection selection;

	private Map<Class, Action> actionsMap;

	public Commander() {
		model = new Model();
		commands = new Commands(model);
		selection = new Selection();
		actionsMap = new HashMap<Class, Action>();
	}

	public Selection getSelection() {
		return selection;
	}

	public Commands getCommands() {
		return commands;
	}

	/**
	 * @return the action associated to the given class
	 */
	public <T extends Action> T getAction(Class<T> actionClass) {
		T action = (T) actionsMap.get(actionClass);
		if (action == null) {
			try {
				action = actionClass.newInstance();
				registerAction(action);
			} catch (Exception e) {
				throw new RuntimeException(
						actionClass
								+ " should have an empty constructor in order to create an instance. If that is not possible, consider register the action usihg #registerAction()");
			}
		}
		return action;
	}

	/**
	 * Register an action
	 */
	public void registerAction(Action action) {
		actionsMap.put(action.getClass(), action);
		action.addedToActions(this);
	}

	/**
	 * Performs the action, identified by its class, with the given arguments
	 * 
	 * @return if the action was performed
	 */
	public boolean perform(Class actionClass, Object... args) {
		Action action = getAction(actionClass);
		if (action == null) {
			throw new RuntimeException(actionClass
					+ " is not a registered action.");
		}

		if (action.isEnabled()) {
			Command command = action.perform(args);
			if (command != null) {
				commands.command(command);
				if (command.modifiesResource()) {
					ArrayList<String> resources = command
							.getResourcesModified();
					if (resources == null) {
						Resource resource = (Resource) selection
								.getSingle(Model.RESOURCE);

						if (resource != null) {
							resource.modify();
						}
					} else {
						for (String id : resources) {
							Resource resource = model.getResource(id);
							if (resource != null) {
								resource.modify();
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Just formats an array of objects for console printing. For debugging only
	 */
	private String prettyPrintArgs(Object... args) {
		if (args == null) {
			return "[]";
		}
		String str = "[";
		for (Object arg : args) {
			str += (arg instanceof String ? "\"" : "")
					+ (arg == null ? "null" : arg.toString())
					+ (arg instanceof String ? "\"" : "") + " , ";
		}
		if (args.length > 0) {
			str = str.substring(0, str.length() - 3);
		}
		str += "]";
		return str;
	}

	/**
	 * Adds a listener to an action. The listener will be notified when the
	 * state of the action changes
	 */
	public void addActionListener(Class actionClass, ActionListener listener) {
		Action action = getAction(actionClass);
		if (action != null) {
			action.addActionListener(listener);
		}
	}

	/**
	 * @return if the given action is enabled
	 */
	public boolean isEnabled(Class actionClass) {
		Action process = getAction(actionClass);
		return process != null && process.isEnabled();
	}

	public Model getModel() {
		return model;
	}
}
