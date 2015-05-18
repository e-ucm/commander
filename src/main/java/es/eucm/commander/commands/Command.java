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

import es.eucm.commander.events.ModelEvent;

import java.util.ArrayList;

/**
 * Defines modifications that can be performed over the model
 */
public abstract class Command {

	private ArrayList<String> resourcesModified;

	private boolean transparent = false;

	/**
	 * When a command is transparent, is automatically undone/redone along with
	 * its previous/next command. Default is false.
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	/**
	 * When a command is transparent, is automatically undone/redone along with
	 * its previous/next command.
	 */
	public boolean isTransparent() {
		return transparent;
	}

	/**
	 * Do the actual work.
	 * 
	 * @return a model event if it could be performed, null in other case.
	 * 
	 */
	public abstract ModelEvent doCommand();

	/**
	 * @return if the action can be undone
	 */
	public abstract boolean canUndo();

	/**
	 * Undo the work done by the action
	 * 
	 * @return a model event if it could be performed, null in other case.
	 */
	public abstract ModelEvent undoCommand();

	/**
	 * Incorporates the tasks of the the given command, if possible.
	 * 
	 * @return if combination succeeded
	 */
	public boolean combine(Command command) {
		return false;
	}

	/**
	 * @return whether this command modifies the current selected resource
	 */
	public boolean modifiesResource() {
		return true;
	}

	/**
	 * @return a list with the ids of the resource modified by this command. If
	 *         {@code null} is returned, and {@link #modifiesResource()} returns
	 *         true, it modifies the default selected resource
	 */
	public ArrayList<String> getResourcesModified() {
		return resourcesModified;
	}

	/**
	 * Add a resource id to the list of resources modified
	 */
	public void addResourceModified(String resourceModified) {
		if (resourcesModified == null) {
			resourcesModified = new ArrayList<String>();
		}
		this.resourcesModified.add(resourceModified);
	}

}
