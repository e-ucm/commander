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
import es.eucm.commander.commands.ListCommand.AddToListCommand;
import es.eucm.commander.commands.ListCommand.ListWrapper;
import es.eucm.commander.commands.ListCommand.RemoveFromListCommand;
import es.eucm.commander.commands.ListCommand.ReorderInListCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Adds an element to an array
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Type}</em> Type of operation (add,
 * remove, reorder)</dd>
 * <dd><strong>args[1]</strong> <em>{@link Object}</em> Parent of the list</dd>
 * <dd><strong>args[2]</strong> <em>{@link Object}</em> The list</dd>
 * <dd><strong>args[3]</strong> <em>{@link Object}</em> the element on which
 * perform the action
 * <dd><strong>args[4]</strong> <em>{@link Number}</em> an index, if present,
 * will be the index where to add/reorder the object. Ignored in remove.</dd>
 * </dl>
 */
public class ModifyList extends Action {

	public enum Type {
		ADD, REMOVE, REORDER
	}

	private Map<Class, ListWrapperFactory> factoryMap = new HashMap<Class, ListWrapperFactory>();

	public ModifyList() {
		registerListWrapperFactory(List.class, new LWrapperFactory());
	}

	/**
	 * Register a wrapper factory able to create a {@link ListWrapper} around
	 * the given class
	 */
	public void registerListWrapperFactory(Class clazz,
			ListWrapperFactory factory) {
		factoryMap.put(clazz, factory);
	}

	@Override
	public Command perform(Object... args) {
		Type type = (Type) args[0];
		Object parent = args[1];
		Object list = args[2];
		Object item = args[3];
		Number index = args.length == 5 ? (Number) args[4] : null;

		ListWrapperFactory factory = getFactory(list.getClass());
		if (factory == null) {
			return null;
		}

		ListWrapper wrapper = factory.wrap(list);
		switch (type) {
		case ADD:
			return index == null ? new AddToListCommand(parent, wrapper, item)
					: new AddToListCommand(parent, wrapper, item,
							index.intValue());
		case REMOVE:
			return new RemoveFromListCommand(parent, wrapper, item);
		case REORDER:
			if (index == null) {
				throw new RuntimeException(
						"Index can be null when reordering a a element in a list");
			}
			return new ReorderInListCommand(parent, wrapper, item,
					index.intValue());
		}
		return null;
	}

	private ListWrapperFactory getFactory(Class clazz) {
		ListWrapperFactory factory = factoryMap.get(clazz);
		if (factory == null) {
			for (Entry<Class, ListWrapperFactory> entry : factoryMap.entrySet()) {
				if (entry.getKey().isAssignableFrom(clazz)) {
					return entry.getValue();
				}
			}
		}
		return factory;
	}

	public interface ListWrapperFactory<T> {
		ListWrapper wrap(T object);
	}

	public static class LWrapperFactory implements ListWrapperFactory<List> {

		@Override
		public ListWrapper wrap(List object) {
			return new LWrapper(object);
		}

		public static class LWrapper implements ListWrapper {

			private List list;

			public LWrapper(List list) {
				this.list = list;
			}

			@Override
			public Object getList() {
				return list;
			}

			@Override
			public void add(Object element) {
				list.add(element);
			}

			@Override
			public void insert(int newIndex, Object element) {
				list.add(newIndex, element);
			}

			@Override
			public int size() {
				return list.size();
			}

			@Override
			public int indexOf(Object element) {
				return list.indexOf(element);
			}

			@Override
			public void remove(Object element) {
				list.remove(element);
			}

			@Override
			public Object get(int i) {
				return list.get(i);
			}
		}
	}
}
