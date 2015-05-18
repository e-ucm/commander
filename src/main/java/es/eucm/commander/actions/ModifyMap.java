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
import es.eucm.commander.commands.MapCommand.MapWrapper;
import es.eucm.commander.commands.MapCommand.PutToMapCommand;
import es.eucm.commander.commands.MapCommand.RemoveFromMapCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Adds an element to an array
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Type}</em> Type of operation (put,
 * remove)</dd>
 * <dd><strong>args[1]</strong> <em>{@link Object}</em> Parent of the map</dd>
 * <dd><strong>args[2]</strong> <em>{@link Object}</em> The map</dd>
 * <dd><strong>args[3]</strong> <em>{@link Object}</em> the key
 * <dd><strong>args[4]</strong> <em>{@link Number}</em> the value. Ignored in
 * remove.</dd>
 * </dl>
 */
public class ModifyMap extends Action {

	public enum Type {
		PUT, REMOVE
	}

	private Map<Class, MapWrapperFactory> factoryMap = new HashMap<Class, MapWrapperFactory>();

	public ModifyMap() {
		registerListWrapperFactory(Map.class, new MWrapperFactory());
	}

	/**
	 * Register a wrapper factory able to create a
	 * {@link es.eucm.commander.commands.ListCommand.ListWrapper} around the
	 * given class
	 */
	public void registerListWrapperFactory(Class clazz,
			MapWrapperFactory factory) {
		factoryMap.put(clazz, factory);
	}

	@Override
	public Command perform(Object... args) {
		Type type = (Type) args[0];
		Object parent = args[1];
		Object map = args[2];
		Object key = args[3];
		Object value = args.length == 5 ? args[4] : null;

		MapWrapperFactory factory = getFactory(map.getClass());
		if (factory == null) {
			return null;
		}

		MapWrapper wrapper = factory.wrap(map);
		switch (type) {
		case PUT:
			return new PutToMapCommand(parent, wrapper, key, value);
		case REMOVE:
			return new RemoveFromMapCommand(parent, wrapper, key);
		}
		return null;
	}

	private MapWrapperFactory getFactory(Class clazz) {
		MapWrapperFactory factory = factoryMap.get(clazz);
		if (factory == null) {
			for (Entry<Class, MapWrapperFactory> entry : factoryMap.entrySet()) {
				if (entry.getKey().isAssignableFrom(clazz)) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	public interface MapWrapperFactory<T> {
		MapWrapper wrap(T object);
	}

	public static class MWrapperFactory implements MapWrapperFactory<Map> {

		@Override
		public MapWrapper wrap(Map map) {
			return new MWrapper(map);
		}

		public static class MWrapper implements MapWrapper {

			private Map map;

			public MWrapper(Map map) {
				this.map = map;
			}

			@Override
			public Object getMap() {
				return map;
			}

			@Override
			public Object remove(Object key) {
				return map.remove(key);
			}

			@Override
			public void put(Object key, Object value) {
				map.put(key, value);
			}

			@Override
			public Object get(Object key) {
				return map.get(key);
			}
		}
	}
}
