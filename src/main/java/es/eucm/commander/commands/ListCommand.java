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

import es.eucm.commander.events.ListEvent;
import es.eucm.commander.events.ListEvent.Type;
import es.eucm.commander.events.ModelEvent;

/**
 * Contains subclasses for adding to, removing from, and reordering elements in
 * lists. Changing existing elements can be achieved via the suitable
 * ChangeFieldCommand
 */
public abstract class ListCommand extends Command {

	private boolean add;

	/**
	 * List owner
	 */
	private Object parent;

	/**
	 * The list in which the added elements will be placed.
	 */
	protected ListWrapper list;

	/**
	 * The element to be added to the list.
	 */
	protected Object element;

	protected int oldIndex;

	protected int newIndex;

	/**
	 * Creates an add to list command
	 * 
	 * @param parent
	 *            owner list
	 * @param list
	 *            the list in which the element will be added
	 * @param element
	 *            the element to be added to the list
	 * @param index
	 *            the index where the element should be added. {@code -1} adds
	 *            the element at the end of the list
	 */
	protected ListCommand(Object parent, ListWrapper list, Object element,
			int index) {
		this(parent, list, element, true, index);
	}

	protected ListCommand(Object parent, ListWrapper list, Object e, boolean add) {
		this(parent, list, e, add, list.size());
	}

	protected ListCommand(Object parent, ListWrapper list, Object e,
			boolean add, int index) {
		this.parent = parent;
		this.add = add;
		this.parent = parent;
		this.list = list;
		this.element = e;
		this.newIndex = index;
	}

	@Override
	public ListEvent doCommand() {
		if (add) {
			if (newIndex > list.size()) {
				newIndex = list.size();
			} else if (newIndex < 0) {
				newIndex = 0;
			}
			list.insert(newIndex, element);
			return new ListEvent(Type.ADDED, parent, list.getList(), element,
					newIndex);
		} else {
			oldIndex = list.indexOf(element);
			if (oldIndex == -1) {
				return null;
			}
			list.remove(element);
			return new ListEvent(Type.REMOVED, parent, list.getList(), element,
					oldIndex);
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		if (add) {
			list.remove(element);
			return new ListEvent(Type.REMOVED, parent, list.getList(), element,
					newIndex);
		} else {
			if (oldIndex == -1) {
				return null;
			}
			list.insert(oldIndex, element);
			return new ListEvent(Type.ADDED, parent, list.getList(), element,
					oldIndex);
		}
	}

	@Override
	public boolean combine(Command command) {
		return false;
	}

	public static class AddToListCommand extends ListCommand {

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param parent
		 *            list owner
		 * @param list
		 *            The list in which the command is to be applied
		 * @param e
		 *            The P element to be added to a list by the command
		 */
		public AddToListCommand(Object parent, ListWrapper list, Object e) {
			super(parent, list, e, true);
		}

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param parent
		 *            list owner
		 * @param list
		 *            The list in which the command should be applied
		 * @param e
		 *            The P element to be added to a list by the command
		 * @param index
		 *            the position to occupy by the element in the list
		 */
		public AddToListCommand(Object parent, ListWrapper list, Object e,
				int index) {
			super(parent, list, e, index);
		}
	}

	public static class RemoveFromListCommand extends ListCommand {

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param parent
		 *            list owner
		 * @param list
		 *            The list in which the command should be applied
		 * @param e
		 *            The P element to be removed from the list by the command
		 */
		public RemoveFromListCommand(Object parent, ListWrapper list, Object e) {
			super(parent, list, e, false);
		}
	}

	public static class ReorderInListCommand extends CompositeCommand {

		/**
		 * Constructor from the command to move an element from one position in
		 * the list, to another. Internally, this generates a
		 * {@link RemoveFromListCommand} and {@link AddToListCommand} where the
		 * specified position
		 * 
		 * @param parent
		 *            list owner
		 * @param list
		 *            The list in which the command should be applied
		 * @param element
		 *            The P element to be added to a list by the command
		 * @param newIndex
		 *            the new position to occupy by the element
		 */
		public ReorderInListCommand(Object parent, ListWrapper list,
				Object element, int newIndex) {
			super(new RemoveFromListCommand(parent, list, element),
					new AddToListCommand(parent, list, element, newIndex));
		}

	}

	public interface ListWrapper {

		Object getList();

		void add(Object element);

		void insert(int newIndex, Object element);

		int size();

		int indexOf(Object element);

		void remove(Object element);

		Object get(int i);
	}

}
