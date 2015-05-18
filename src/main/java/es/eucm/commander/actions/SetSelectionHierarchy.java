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

/**
 * Sets the selection used the passed hierarchy.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> parent context id</dd>
 * <dd><strong>args[2n - 1; n >= 1]</strong> <em>{@link String}</em> context id.
 * This context will have as parent the context in 2n - 3 or the value in
 * args[0] y n == 1</dd>
 * <dd><strong>args[2n; n >= 1]</strong> <em>{@link Object}</em> object for the
 * context.</dd>
 * </dl>
 */
public class SetSelectionHierarchy extends MultipleAction {

	@Override
	public void performActions(Object... args) {
		String parent = (String) args[0];
		for (int i = 1; i < args.length - 1; i += 2) {
			String context = (String) args[i];
			Object selection = args[i + 1];
			action(SetSelection.class, parent, context, selection);
			parent = context;
		}
	}
}
