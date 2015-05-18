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
import es.eucm.commander.Model.ModelListener;
import es.eucm.commander.actions.AddResource;
import es.eucm.commander.actions.Redo;
import es.eucm.commander.actions.RemoveResource;
import es.eucm.commander.actions.Undo;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ResourceEventTest {

	@Test
	public void test() {
		Commander commander = new Commander();

		View view = new View();
		commander.getModel().addListener(ResourceEvent.class, view);

		commander.perform(AddResource.class, "textFile", "myfile.txt",
				"Sample text");

		assertEquals(1, view.views.size());
		assertEquals("Sample text", view.views.get(0).text);

		commander.perform(RemoveResource.class, "myfile.txt");
		assertEquals(0, view.views.size());

		commander.perform(Undo.class);
		assertEquals(1, view.views.size());
		assertEquals("Sample text", view.views.get(0).text);
		commander.perform(Redo.class);
		assertEquals(0, view.views.size());

		commander.perform(Undo.class);
		commander.perform(Undo.class);
		assertEquals(0, view.views.size());
	}

	public static class TextView {
		String fileName;
		String text;

		public TextView(String fileName, String text) {
			this.fileName = fileName;
			this.text = text;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof TextView
					&& ((TextView) obj).fileName.equals(this.fileName);
		}
	}

	public static class View implements ModelListener<ResourceEvent> {

		private ArrayList<TextView> views = new ArrayList<TextView>();

		@Override
		public void modelChanged(ResourceEvent event) {
			switch (event.getType()) {
			case ADDED:
				views.add(new TextView(event.getId(), (String) event
						.getResource()));
				break;
			case REMOVED:
				views.remove(new TextView(event.getId(), null));
				break;
			}

		}
	}
}
