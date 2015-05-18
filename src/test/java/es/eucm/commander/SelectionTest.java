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

import es.eucm.commander.Selection.Context;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SelectionTest {

	private static Entity scene;
	private static Entity child;
	private static SubEntity component;
	private static SubEntity component1;

	private Selection selection;

	@BeforeClass
	public static void setUpSelection() {
		scene = new Entity();
		child = new Entity();
		component = new SubEntity();
		component1 = new SubEntity();
	}

	@Before
	public void setUp() {
		selection = new Selection();
	}

	@Test
	public void testSimple() {

		selection.set(null, "scene", scene);
		assertEquals(1, selection.getContexts().size());
		selection.set("scene", "editedGroup", scene);
		assertEquals(2, selection.getContexts().size());
		selection.set("editedGroup", "sceneElement", child);
		assertEquals(3, selection.getContexts().size());
		selection.set("sceneElement", "component", component);
		assertEquals(4, selection.getContexts().size());

		assertSame(selection.get("scene")[0], scene);
		assertSame(selection.get("component")[0], component);

		selection.set("sceneElement", "component", component1);
		assertEquals(4, selection.getContexts().size());
		assertSame(selection.get("component")[0], component1);

		selection.set("scene", "editedGroup", scene);
		assertEquals(4, selection.getContexts().size());
		assertSame(selection.getCurrent()[0], scene);
		selection.set("scene", "editedGroup", new Entity());
		assertEquals(2, selection.getContexts().size());
	}

	@Test
	public void testRemove() {

		selection.set(null, "scene", scene);
		selection.set("scene", "editedGroup", scene);
		selection.set("editedGroup", "sceneElement", child);
		selection.set("sceneElement", "component", child);

		assertEquals(4, selection.getContexts().size());

		ArrayList<Context> contextRemoved = selection.set("scene",
				"editedGroup", new Entity());
		assertEquals(2, selection.getContexts().size());
		assertEquals(3, contextRemoved.size());

		selection.set("scene", "editedGroup", scene);
		selection.set("editedGroup", "sceneElement", child);
		selection.set("sceneElement", "component", child);

		assertEquals(4, selection.getContexts().size());

		contextRemoved = selection.set("editedGroup", "whatever", component);
		assertEquals(3, selection.getContexts().size());
		assertEquals(2, contextRemoved.size());

	}
}
