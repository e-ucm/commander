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

import es.eucm.commander.Selection;
import es.eucm.commander.commands.Command;

/**
 * Executes an action using as arguments objects in the selection.
 * {@link String} arguments are converted to the value returned by
 * {@link Selection#getSingle(String)}. If the value is null, then the string
 * untransformed is passed as argument. No {@link String} arguments are passed
 * as is.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Class}</em> Class of the action to
 * execute</dd>
 * <dd><strong>args[1..n]</strong> <em>{@link String}</em> Arguments for the
 * action in args[0]</dd>
 * </dl>
 */
public class SelectionAction extends Action {
	@Override
	public Command perform(Object... args) {
		Class actionClass = (Class) args[0];
		Object[] args2 = new Object[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof String
					&& commander.getSelection().getSingle((String) arg) != null) {
				arg = commander.getSelection().getSingle((String) arg);
			}
			args2[i - 1] = arg;
		}
		Action action = commander.getAction(actionClass);
		return action.perform(args2);
	}
}
