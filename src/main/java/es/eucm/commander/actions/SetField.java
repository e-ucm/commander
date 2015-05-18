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

import es.eucm.commander.commands.Command;
import es.eucm.commander.commands.FieldCommand;

/**
 * Sets a value in an objects field
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Object}</em> target object whose
 * field will be set</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> field name</dd>
 * <dd><strong>args[2]</strong> <em>{@link Object}</em> new value for the field</dd>
 * <dd><strong>args[3]</strong>
 * <em>{@link Object} (Optional, default <pre>false</pre>)</em> if this command
 * should be combined with the previous one</dd>
 * </dl>
 */
public class SetField extends Action {

	@Override
	public Command perform(Object... args) {
		boolean combine = args.length == 4 ? (Boolean) args[3] : false;
		return new FieldCommand(args[0], (String) args[1], args[2], combine);
	}
}
