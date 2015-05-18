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
package es.eucm.commander;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Keeps track of the elements selected in the model, in different contexts.
 * Contexts are hierarchical, so every time a selection is set, the parent
 * context must be known.
 */
public class Selection {

	private static final Object[] NO_SELECTION = new Object[0];

	private int pointer = -1;

	private ArrayList<Context> contexts = new ArrayList<Context>();

	/**
	 * <p>
	 * Sets the selection of the given context, and sets the focus to this
	 * context (i.e., {@link #getCurrent()} will return the selection for this
	 * context). This method removes all those contexts that are below from it.
	 * For example, if we have the following context structure:
	 * </p>
	 * 
	 * <pre>
	 * scene + editedGroup + sceneElement
	 * </pre>
	 * 
	 * <p>
	 * and we set the "scene" context to a new scene, "editedGroup" and
	 * "sceneElement" contexts are removed and returned by the method.
	 * <p>
	 * <p>
	 * There is a special case in where children context are not removed: when
	 * the selection stays the same (e.g., we set a scene and the "scene"
	 * context already has that scene as selection), children context are not
	 * removed. The only change the method produce then is that the selection
	 * focus passes to the "scene" context.
	 * </p>
	 * 
	 * @param parentContextId
	 *            the id of the parent context
	 * @param contextId
	 *            the id of the context to be changed
	 * @param selection
	 *            the selection for the context
	 * @return the existing contexts that has been removed to set the new
	 *         context
	 */
	public ArrayList<Context> set(String parentContextId, String contextId,
			Object... selection) {

		if (parentContextId == null) {
			ArrayList<Context> contextsRemoved = contexts;
			contexts = new ArrayList<Context>();
			contexts.add(new Context(null, contextId, selection));
			this.pointer = 0;
			return contextsRemoved;
		}

		boolean contextPresent = false;
		ArrayList<Context> contextsRemoved = new ArrayList<Context>();

		int index = getIndex(contextId);
		Context context;
		if (index == -1) {
			int parentIndex = getIndex(parentContextId);
			context = new Context(parentContextId, contextId);
			if (parentIndex != -1 && parentIndex < contexts.size() - 1) {
				for (int i = parentIndex + 1; i < contexts.size(); i++) {
					contextsRemoved.add(contexts.get(i));
				}
				contexts.subList(parentIndex + 1, contexts.size()).clear();
			}
			contexts.add(context);
			index = contexts.size() - 1;
		} else {
			contextPresent = true;
			context = contexts.get(index);
		}

		if (context.isDifferentSelection(selection)) {
			if (contextPresent) {
				Context oldContext = new Context(context.getParentId(),
						context.getId(), context.getSelection());
				contextsRemoved.add(oldContext);
			}
			context.setSelection(selection);
			if (index + 1 < contexts.size()) {
				for (int i = index + 1; i < contexts.size(); i++) {
					contextsRemoved.add(contexts.get(i));
				}
				contexts.subList(index + 1, contexts.size()).clear();
			}
		}
		this.pointer = index;
		return contextsRemoved;
	}

	private int getIndex(String id) {
		int j = 0;
		for (Context context : contexts) {
			if (context.getId().equals(id)) {
				return j;
			}
			j++;
		}
		return -1;
	}

	/**
	 * @return current edition context
	 */
	public Context getCurrentContext() {
		return pointer == -1 ? null : contexts.get(pointer);
	}

	public String getCurrentContextId() {
		return getCurrentContext() == null ? null : getCurrentContext().getId();
	}

	/**
	 * @return array of the objects selected in the focused context. Never
	 *         returns {@code null}. This array should not be modified
	 */
	public Object[] getCurrent() {
		if (pointer < 0 || pointer > contexts.size() - 1) {
			return NO_SELECTION;
		} else {
			return contexts.get(pointer).getSelection();
		}
	}

	public Object getCurrentSingle() {
		Object[] selection = getCurrent();
		if (selection.length == 0) {
			return null;
		} else {
			return selection[0];
		}
	}

	/**
	 * @return the selection of an specific context. It the context is not set,
	 *         never returns {@code null}, but an empty array. Returned array
	 *         should not be modified.
	 */
	public Object[] get(String contextId) {
		int index = getIndex(contextId);
		return index == -1 ? NO_SELECTION : contexts.get(getIndex(contextId))
				.getSelection();
	}

	/**
	 * @return the first element of the selection of a context. Can be
	 *         {@code null} if not selection is present in the given context
	 */
	public Object getSingle(String contextId) {
		Object[] selection = get(contextId);
		return selection == null || selection.length == 0 ? null : selection[0];
	}

	/**
	 * @return whether an object is within a context
	 */
	public boolean contains(String contextId, Object object) {
		Object[] selection = get(contextId);
		if (selection != null) {
			for (Object o : selection) {
				if (o == object) {
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<Context> getContexts() {
		return contexts;
	}

	public Context getContext(String contextId) {
		int index = getIndex(contextId);
		return index == -1 ? null : contexts.get(index);
	}

	public Context remove(String contextId) {
		int index = getIndex(contextId);

		if (index == -1) {
			return null;
		}

		if (index == pointer) {
			pointer--;
		}
		return contexts.remove(index);
	}

	public void clear() {
		contexts.clear();
		pointer = -1;
	}

	public static class Context {

		private String parentId;

		private String id;

		private Object[] selection;

		public Context(String parentId, String id, Object... selection) {
			this.parentId = parentId;
			this.id = id;
			this.selection = selection;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public void setSelection(Object[] selection) {
			this.selection = selection;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Object[] getSelection() {
			return selection;
		}

		public boolean isDifferentSelection(Object... selection) {
			if (this.selection.length == selection.length) {
				for (int i = 0; i < selection.length; i++) {
					if (!this.selection[i].equals(selection[i])) {
						return true;
					}
				}
				return false;
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Context that = (Context) o;

			return !(id != null ? !id.equals(that.id) : that.id != null)
					&& !(parentId != null ? !parentId.equals(that.parentId)
							: that.parentId != null)
					&& Arrays.equals(selection, that.selection);
		}
	}
}
