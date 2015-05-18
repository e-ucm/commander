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

import es.eucm.commander.commands.SelectionCommand;

import java.util.List;

/**
 * Sets the current selection
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> parent context id</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> context id</dd>
 * <dd><strong>args[2]</strong> <em>{@link List}</em> list with the objects for
 * the selection.
 * <dd>or</dd>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> parent context id</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> context id</dd>
 * <dd><strong>args[2..n]</strong> <em>{@link Object}</em> objects for the
 * selection. Cannot be null.</dd>
 * </dl>
 */
public class SetSelection extends Action {
	@Override
	public SelectionCommand perform(Object... args) {
		if (args.length == 3 && args[2] instanceof List) {
			return new SelectionCommand(commander, (String) args[0],
					(String) args[1], ((List) args[2]).toArray());
		} else {
			Object[] selection = new Object[args.length - 2];
			System.arraycopy(args, 2, selection, 0, selection.length);
			return new SelectionCommand(commander, (String) args[0],
					(String) args[1], selection);
		}
	}
}
