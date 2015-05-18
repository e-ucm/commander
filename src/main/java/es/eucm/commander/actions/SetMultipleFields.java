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
import es.eucm.commander.commands.MultipleFieldsCommand;

/**
 * Sets multiples values in multiples fields.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Object}</em> target object whose
 * field will be set</dd>
 * <dd><strong>args[2n - 1; n>0]</strong> <em>{@link String}</em> field name</dd>
 * <dd><strong>args[2n; n > 0]</strong> <em>{@link Object}</em> new value for
 * the field in 2n + 1</dd>
 * <dd><strong>args[k]</strong>
 * <em>{@link Object} (Optional, default <code>false</code>)</em> If the command
 * must be combined with the previous one</dd>
 * </dl>
 */
public class SetMultipleFields extends Action {

	@Override
	public Command perform(Object... args) {
		boolean combine = args.length % 2 == 0 ? (Boolean) args[args.length - 1]
				: false;
		MultipleFieldsCommand command = new MultipleFieldsCommand(args[0],
				combine);
		for (int i = 1; i < args.length - 1; i += 2) {
			command.field((String) args[i], args[i + 1]);
		}
		return command;
	}
}
