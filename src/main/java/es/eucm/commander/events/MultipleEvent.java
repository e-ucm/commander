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
package es.eucm.commander.events;

import java.util.ArrayList;

public class MultipleEvent implements ModelEvent {

	private ArrayList<ModelEvent> events;

	public MultipleEvent() {
		this.events = new ArrayList<ModelEvent>();
	}

	public void addEvent(ModelEvent event) {
		events.add(event);
	}

	public ArrayList<ModelEvent> getEvents() {
		return events;
	}

	@Override
	public Object getTarget() {
		// MultipleEvents are collections of events.Their target is irrelevant.
		return null;
	}
}
