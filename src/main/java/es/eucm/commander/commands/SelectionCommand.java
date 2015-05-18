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

import es.eucm.commander.Commander;
import es.eucm.commander.Selection;
import es.eucm.commander.Selection.Context;
import es.eucm.commander.events.MultipleEvent;
import es.eucm.commander.events.SelectionEvent;
import es.eucm.commander.events.SelectionEvent.Type;

import java.util.ArrayList;

/**
 * A command to change the selection
 */
public class SelectionCommand extends Command {

	private Commander commander;

	private String parentContextId;

	private String contextId;

	private Object[] selection;

	private ArrayList<Context> contextsRemoved;

	private Context oldContext;

	private boolean added;

	public SelectionCommand(Commander commander, String parentContextId,
			String contextId, Object... selection) {
		this.commander = commander;
		this.parentContextId = parentContextId;
		this.contextId = contextId;
		this.selection = selection;
	}

	@Override
	public MultipleEvent doCommand() {
		Selection selection = commander.getSelection();
		Context currentContext = selection.getCurrentContext();

		if (currentContext != null) {
			oldContext = new Context(currentContext.getParentId(),
					currentContext.getId(), currentContext.getSelection());
		}

		added = selection.getContext(contextId) == null;

		contextsRemoved = selection.set(parentContextId, contextId,
				this.selection);

		if (!added) {
			for (Context context : contextsRemoved) {
				if (contextId.equals(context.getId())) {
					added = true;
					break;
				}
			}
		}

		MultipleEvent multipleEvent = new MultipleEvent();

		if (oldContext != null
				&& oldContext.equals(selection.getCurrentContext())) {
			return multipleEvent;
		}

		for (Context context : contextsRemoved) {
			multipleEvent.addEvent(new SelectionEvent(Type.REMOVED, context
					.getParentId(), context.getId(), context.getSelection()));
		}

		if (added) {
			multipleEvent.addEvent(new SelectionEvent(Type.ADDED,
					parentContextId, contextId, this.selection));
		}
		multipleEvent.addEvent(new SelectionEvent(Type.FOCUSED,
				parentContextId, contextId, this.selection));
		return multipleEvent;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public MultipleEvent undoCommand() {
		Selection selection = commander.getSelection();
		MultipleEvent multipleEvent = new MultipleEvent();

		if (added) {
			// Selection commands can be discontinuous, so it could happen the
			// context added is not present in the selection
			Context context = selection.remove(contextId);
			if (context != null) {
				multipleEvent.addEvent(new SelectionEvent(Type.REMOVED,
						parentContextId, contextId, this.selection));
			}
		}

		for (Context context : contextsRemoved) {
			selection.set(context.getParentId(), context.getId(),
					context.getSelection());
			multipleEvent.addEvent(new SelectionEvent(Type.ADDED, context
					.getParentId(), context.getId(), context.getSelection()));
		}

		if (oldContext != null) {
			selection.set(oldContext.getParentId(), oldContext.getId(),
					oldContext.getSelection());
			multipleEvent.addEvent(new SelectionEvent(Type.FOCUSED, oldContext
					.getParentId(), oldContext.getId(), oldContext
					.getSelection()));
		}
		return multipleEvent;
	}

	@Override
	public boolean modifiesResource() {
		return false;
	}

	@Override
	public boolean combine(Command command) {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}
}
