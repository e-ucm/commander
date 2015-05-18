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

import es.eucm.commander.Model;
import es.eucm.commander.Resource;
import es.eucm.commander.events.ModelEvent;
import es.eucm.commander.events.ResourceEvent;
import es.eucm.commander.events.ResourceEvent.Type;

import java.util.ArrayList;

/**
 * Adds/removes resource to the Model
 */
public class ResourceCommand extends Command {

	private static final ArrayList<String> EMPTY_LIST = new ArrayList<String>();

	private boolean createResourceModified = true;

	private Model model;

	private String id;

	private Object resourceObject;

	private String category;

	private boolean add;

	public ResourceCommand(Model model, String category, String id,
			Object resourceObject, boolean add) {
		this.model = model;
		this.id = id;
		this.resourceObject = resourceObject;
		this.category = category;
		this.add = add;
	}

	public void setCreateResourceModified(boolean createResourceModified) {
		this.createResourceModified = createResourceModified;
	}

	@Override
	public ModelEvent doCommand() {
		Resource resource;
		if (add) {
			resource = model.putResource(category, id, resourceObject);
			if (createResourceModified) {
				resource.modify();
			} else {
				resource.commit();
			}
		} else {
			resource = model.removeResource(id);
			resourceObject = resource.getObject();
			category = resource.getCategory();
		}
		return new ResourceEvent(add ? Type.ADDED : Type.REMOVED, id,
				resourceObject, category);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		if (add) {
			model.removeResource(id);
		} else {
			Resource resource = model.putResource(category, id, resourceObject);
			resource.modify();
		}
		return new ResourceEvent(add ? Type.REMOVED : Type.ADDED, id,
				resourceObject, category);
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}

	@Override
	public boolean modifiesResource() {
		return false;
	}

	public static class AddResourceCommand extends ResourceCommand {

		public AddResourceCommand(Model model, String category, String id,
				Object resource) {
			super(model, category, id, resource, true);
		}
	}

	public static class RemoveResourceCommand extends ResourceCommand {

		public RemoveResourceCommand(Model model, String id) {
			super(model, null, id, null, false);
		}
	}
}
