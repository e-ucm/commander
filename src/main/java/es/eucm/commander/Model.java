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

import es.eucm.commander.Resource.State;
import es.eucm.commander.events.CommitEvent;
import es.eucm.commander.events.FieldEvent;
import es.eucm.commander.events.ModelEvent;
import es.eucm.commander.events.MultipleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Editor model. Contains all the resources of the current game project.
 */
public class Model {

	public static final String RESOURCE = "_resource";

	private ArrayList<Resource> auxResources = new ArrayList<Resource>();

	private IdentityHashMap<Object, Map<Class, ArrayList<ModelListener>>> listenersMap = new IdentityHashMap<Object, Map<Class, ArrayList<ModelListener>>>();

	private Map<String, Map<String, Resource>> resourcesMap = new HashMap<String, Map<String, Resource>>();

	private Committer committer;

	public void setCommitter(Committer committer) {
		this.committer = committer;
	}

	public <T extends ModelEvent> void addListener(Class<T> eventClass,
			ModelListener<T> listener) {
		addListener(null, eventClass, listener);
	}

	/**
	 * Adds a listener to specific target. These listeners will only be notified
	 * when {@link ModelEvent#getTarget()} is the passed target
	 * 
	 * @param target
	 *            the object that must be listened
	 * @param eventClass
	 *            the type of events to listen
	 * @param listener
	 *            the listener
	 */
	public <T extends ModelEvent> void addListener(Object target,
			Class<T> eventClass, ModelListener<T> listener) {
		Map<Class, ArrayList<ModelListener>> map = this.listenersMap
				.get(target);
		if (map == null) {
			map = new HashMap<Class, ArrayList<ModelListener>>();
			listenersMap.put(target, map);
		}

		ArrayList<ModelListener> listeners = map.get(eventClass);
		if (listeners == null) {
			listeners = new ArrayList<ModelListener>();
			map.put(eventClass, listeners);
		}

		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes the listener from all targets in the model
	 */
	public void removeListener(ModelListener listener) {
		for (Map<Class, ArrayList<ModelListener>> map : listenersMap.values()) {
			for (ArrayList<ModelListener> listeners : map.values()) {
				listeners.remove(listener);
			}
		}
	}

	/**
	 * Remos the given listener from its current targets and adds it to the new
	 * target
	 */
	public <T extends ModelEvent> void retargetListener(Object newTarget,
			Class<T> eventClass, ModelListener<T> listener) {
		removeListener(listener);
		addListener(newTarget, eventClass, listener);
	}

	public Resource putResource(String category, String id,
			Object resourceObject) {
		Resource resource = new Resource(category, id, resourceObject);
		Map<String, Resource> map = getResources(category);
		map.put(id, resource);
		return resource;
	}

	public Resource removeResource(String id) {
		Resource resource = getResource(id);
		resource.remove();
		return resource;
	}

	public void commit() {
		for (Map<String, Resource> map : resourcesMap.values()) {
			for (Iterator<Entry<String, Resource>> it = map.entrySet()
					.iterator(); it.hasNext();) {
				Map.Entry<String, Resource> entry = it.next();
				Resource resource = entry.getValue();
				switch (resource.getState()) {
				case MODIFIED:
					if (committer != null) {
						committer.commitModified(resource);
					}
					resource.commit();
					break;
				case REMOVED:
					if (committer != null) {
						committer.commitRemoved(resource);
					}
					it.remove();
				}
			}
		}
		notify(new CommitEvent());
	}

	public Resource getResource(String id) {
		for (Entry<String, Map<String, Resource>> entry : resourcesMap
				.entrySet()) {
			if (entry.getValue().containsKey(id)) {
				Resource resource = entry.getValue().get(id);
				return resource.getState() != State.REMOVED ? resource : null;
			}
		}
		return null;
	}

	/**
	 * @return a valid resource id in the given category
	 */
	public String createId(String category) {
		Map<String, Resource> resourcesMap = getResources(category);
		int count = 0;
		String id;
		do {
			id = category + count++ + ".json";
		} while (resourcesMap.containsKey(id));
		return id;
	}

	private Map<String, Resource> getResources(String category) {
		Map<String, Resource> map = resourcesMap.get(category);
		if (map == null) {
			map = new HashMap<String, Resource>();
			resourcesMap.put(category, map);
		}
		return map;
	}

	/**
	 * Notifies a model event to listeners. If the event is instance of
	 * {@link MultipleEvent}, each of the events that contains is individually
	 * notified.
	 * 
	 * @param event
	 *            the event to notify. Could be {@code null}
	 */
	@SuppressWarnings("unchecked")
	public void notify(ModelEvent event) {
		if (event != null) {
			if (event instanceof MultipleEvent) {
				for (ModelEvent e : ((MultipleEvent) event).getEvents()) {
					notify(e);
				}
			} else {
				notifyListeners(event, event.getTarget());
			}
		}
	}

	private void notifyListeners(ModelEvent event, Object target) {
		Map<Class, ArrayList<ModelListener>> map = listenersMap.get(target);
		if (map == null) {
			return;
		}
		ArrayList<ModelListener> listeners = map.get(event.getClass());
		if (listeners != null) {
			for (ModelListener listener : listeners) {
				if (event instanceof FieldEvent
						&& listener instanceof FieldListener) {
					if (((FieldListener) listener)
							.listenToField(((FieldEvent) event).getField())) {
						listener.modelChanged(event);
					}
				} else {
					listener.modelChanged(event);
				}
			}
		}
	}

	/**
	 * @return a list with all the resources in a category. This list should not
	 *         be modified or cached
	 */
	public List<Resource> getCategoryResources(String category) {
		auxResources.clear();
		Map<String, Resource> resources = resourcesMap.get(category);
		if (resources != null) {
			for (Resource resource : resources.values()) {
				if (resource.getState() != State.REMOVED) {
					auxResources.add(resource);
				}
			}
		}
		return auxResources;
	}

	/**
	 * @return a list with all the resources in the model. This list should not
	 *         be modified or cached
	 */
	public List<Resource> getAllResources() {
		auxResources.clear();
		for (Map<String, Resource> resources : resourcesMap.values()) {
			for (Resource resource : resources.values()) {
				if (resource.getState() != State.REMOVED) {
					auxResources.add(resource);
				}
			}
		}
		return auxResources;
	}

	/**
	 * General interface to listen to the model
	 * 
	 * @param <T>
	 *            the type of the event
	 */
	public interface ModelListener<T extends ModelEvent> {

		/**
		 * Called when the model changed
		 * 
		 * @param event
		 *            the model event
		 */
		void modelChanged(T event);
	}

	/**
	 * General interface to listen to fields
	 */
	public interface FieldListener extends ModelListener<FieldEvent> {

		/**
		 * @param fieldName
		 *            the field name
		 * @return true if this listener is interested in the fieldName
		 */
		boolean listenToField(String fieldName);

	}
}
