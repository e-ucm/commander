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

import es.eucm.commander.Commander;
import es.eucm.commander.Entity;
import es.eucm.commander.Model.ModelListener;
import es.eucm.commander.actions.Redo;
import es.eucm.commander.actions.SetField;
import es.eucm.commander.actions.SetSelection;
import es.eucm.commander.actions.Undo;
import es.eucm.commander.events.SelectionEvent.Type;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class SelectionTestEvent {

	private Commander commander;

	private SelectionView view;

	@Before
	public void setUp() {
		commander = new Commander();
		view = new SelectionView();
		commander.getModel().addListener(SelectionEvent.class, view);
	}

	@Test
	public void testSimple() {

		commander.perform(SetSelection.class, null, "root", new Entity());

		SelectionView.Changes changes = view.changes.get("root");
		assertEquals(changes.removed, 0);
		assertEquals(changes.added, 1);
		assertEquals(changes.focused, 1);
		assertEquals(changes.sequence.get(0), Type.ADDED);
		assertEquals(changes.sequence.get(1), Type.FOCUSED);

		commander.perform(Undo.class);
		assertEquals(changes.removed, 1);
		assertEquals(changes.added, 1);
		assertEquals(changes.focused, 1);
		assertEquals(changes.sequence.get(2), Type.REMOVED);
	}

	@Test
	public void testSelectionSequence() {
		Entity e = new Entity();
		Entity e2 = new Entity();
		commander.perform(SetSelection.class, null, "root", e);
		commander.perform(SetField.class, e, "stringValue", "value");
		commander.perform(SetSelection.class, null, "root", e2);

		SelectionView.Changes changes = view.changes.get("root");
		assertEquals(changes.sequence.get(0), Type.ADDED);
		assertEquals(changes.sequence.get(1), Type.FOCUSED);
		assertEquals(changes.sequence.get(2), Type.REMOVED);
		assertEquals(changes.sequence.get(3), Type.ADDED);
		assertEquals(changes.sequence.get(4), Type.FOCUSED);

		commander.perform(Undo.class);
		assertEquals(commander.getSelection().getSingle("root"), e);

		assertEquals(changes.sequence.get(5), Type.REMOVED);
		assertEquals(changes.sequence.get(6), Type.ADDED);
		assertEquals(changes.sequence.get(7), Type.FOCUSED);
		commander.perform(Redo.class);
		assertEquals(commander.getSelection().getSingle("root"), e2);

	}

	public static class SelectionView implements ModelListener<SelectionEvent> {

		public static class Changes {
			int added;
			int removed;
			int focused;
			ArrayList<Type> sequence = new ArrayList<Type>();
		}

		private HashMap<String, Changes> changes = new HashMap<String, Changes>();

		public SelectionView() {
			changes.put("root", new Changes());
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			Changes c = changes.get(event.getContextId());
			c.sequence.add(event.getType());
			switch (event.getType()) {
			case ADDED:
				c.added++;
				break;
			case REMOVED:
				c.removed++;
				break;
			case FOCUSED:
				c.focused++;
				break;
			}
		}
	}
}
