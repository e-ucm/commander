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
import es.eucm.commander.commands.Command;

import java.util.ArrayList;

/**
 * An action represent a set of changes in the model, represented by the
 * {@link Command} returned in {@link Action#perform}
 */
public abstract class Action {

	protected Commander commander;

	private ArrayList<ActionListener> listeners;

	private boolean enabled;

	public Action() {
		this(true);
	}

	/**
	 * @param enabled
	 *            if the action starts enabled
	 */
	public Action(boolean enabled) {
		this.enabled = enabled;
		this.listeners = new ArrayList<ActionListener>();
	}

	/**
	 * Called when this action is added to the current set of actions
	 */
	public void addedToActions(Commander commander) {
		this.commander = commander;
	}

	/**
	 * Sets whether this action is enabled and can be invoked from the editor
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			for (ActionListener listener : listeners) {
				listener.enableUpdated(getClass(), this.enabled);
			}
		}
	}

	/**
	 * 
	 * @return if this action is enabled and can be invoked by from the editor.
	 *         {@code false} by default
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Adds a listener to the action
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Executes the action with the given arguments
	 * 
	 * @param args
	 *            arguments for the action
	 */
	public abstract Command perform(Object... args);

	/**
	 * General interface to listen to changes in actions' state
	 */
	public interface ActionListener {

		/**
		 * The state of the action changed
		 * 
		 * @param actionClass
		 *            the action class
		 * @param enabled
		 *            if the action is enabled
		 */
		void enableUpdated(Class actionClass, boolean enabled);
	}
}
