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
import es.eucm.commander.Model;
import es.eucm.commander.commands.Command;
import es.eucm.commander.commands.ResourceCommand.AddResourceCommand;

/**
 * Adds a resource in the model
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> Category</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> Resource id</dd>
 * <dd><strong>args[2]</strong> <em>{@link Object}</em> Resource object</dd>
 * <dd><strong>args[3]</strong> <em>{@link Boolean} (Optional)</em> If the
 * resource must be set as modified after creation. Default is true. You might
 * want to set this to false when you just loaded the resource from disk,
 * meaning it should not be saved in the next model commit, unless is modified.</dd>
 * </dl>
 */
public class AddResource extends Action {

	private Model model;

	@Override
	public void addedToActions(Commander commander) {
		model = commander.getModel();
	}

	@Override
	public Command perform(Object... args) {
		String category = (String) args[0];
		String id = (String) args[1];
		Object resourceObject = args[2];
		boolean modified = args.length == 4 ? (Boolean) args[3] : true;
		AddResourceCommand command = new AddResourceCommand(model, category,
				id, resourceObject);
		command.setCreateResourceModified(modified);
		return command;
	}
}
