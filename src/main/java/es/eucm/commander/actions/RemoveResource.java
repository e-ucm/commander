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
import es.eucm.commander.commands.ResourceCommand.RemoveResourceCommand;

/**
 * Removes a resource from the model.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> id of the resource to
 * remove.</dd>
 * </dl>
 */
public class RemoveResource extends Action {

	private Model model;

	@Override
	public void addedToActions(Commander commander) {
		model = commander.getModel();
	}

	@Override
	public Command perform(Object... args) {
		String id = (String) args[0];
		return new RemoveResourceCommand(model, id);
	}
}
