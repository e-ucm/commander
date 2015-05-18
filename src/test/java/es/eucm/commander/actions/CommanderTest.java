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

import es.eucm.commander.Commander;
import es.eucm.commander.Entity;
import es.eucm.commander.Selection;
import es.eucm.commander.actions.ModifyList.Type;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CommanderTest {

	Commander commander;

	@Before
	public void setUpClass() {
		commander = new Commander();
	}

	@Test
	public void testSetField() {
		Entity entity = new Entity();
		commander.perform(SetField.class, entity, "stringValue", "value");
		assertEquals(entity.getStringValue(), "value");
		commander.perform(SetField.class, entity, "stringValue", "value2");
		assertEquals(entity.getStringValue(), "value2");
		commander.perform(Undo.class);
		assertEquals(entity.getStringValue(), "value");

		// Combine
		commander.perform(SetField.class, entity, "stringValue", "start");
		commander.perform(SetField.class, entity, "stringValue", "valueTwo",
				true);
		commander.perform(SetField.class, entity, "stringValue", "valueThree",
				true);

		assertEquals(entity.getStringValue(), "valueThree");
		commander.perform(Undo.class);
		assertEquals(entity.getStringValue(), "start");
		commander.perform(Redo.class);
		assertEquals(entity.getStringValue(), "valueThree");
	}

	@Test
	public void testModifyList() {
		ArrayList<String> list = new ArrayList<String>();
		commander.perform(ModifyList.class, Type.ADD, null, list, "abc");
		commander.perform(ModifyList.class, Type.ADD, null, list, "cde");
		commander.perform(ModifyList.class, Type.ADD, null, list, "rev", 0);

		assertEquals(3, list.size());
		assertTrue(list.contains("abc"));
		assertTrue(list.contains("cde"));
		assertTrue(list.contains("rev"));
		assertEquals(0, list.indexOf("rev"));
		assertEquals(1, list.indexOf("abc"));
		assertEquals(2, list.indexOf("cde"));

		commander.perform(ModifyList.class, Type.REMOVE, null, list, "cde");
		assertEquals(2, list.size());
		assertTrue(list.contains("abc"));
		assertTrue(list.contains("rev"));

		commander.perform(ModifyList.class, Type.REORDER, null, list, "abc", 0);
		assertEquals(0, list.indexOf("abc"));

		// Wrong indexes
		commander.perform(ModifyList.class, Type.REORDER, null, list, "abc",
				5000);
		assertEquals(list.size() - 1, list.indexOf("abc"));

		commander.perform(ModifyList.class, Type.REORDER, null, list, "abc",
				-26);
		assertEquals(0, list.indexOf("abc"));

		commander.perform(ModifyList.class, Type.REMOVE, null, list, "ñor");

		commander.perform(Undo.class);
		assertEquals(0, list.indexOf("abc"));
		commander.perform(Undo.class);
		assertEquals(list.size() - 1, list.indexOf("abc"));
		commander.perform(Undo.class);
		assertEquals(0, list.indexOf("abc"));
		commander.perform(Undo.class);
		assertEquals(2, list.size());
		assertTrue(list.contains("abc"));
		assertTrue(list.contains("rev"));
		commander.perform(Undo.class);
		assertEquals(3, list.size());
		assertTrue(list.contains("abc"));
		assertTrue(list.contains("cde"));
		assertTrue(list.contains("rev"));
		assertEquals(0, list.indexOf("rev"));
		assertEquals(1, list.indexOf("abc"));
		assertEquals(2, list.indexOf("cde"));
		commander.perform(Undo.class);
		assertEquals(2, list.size());
		commander.perform(Undo.class);
		assertEquals(1, list.size());
		commander.perform(Undo.class);
		assertEquals(0, list.size());
	}

	@Test
	public void testModifyMap() {
		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 1; i <= 5; i++) {
			commander.perform(ModifyMap.class, ModifyMap.Type.PUT, null, map,
					"key" + i, "value" + i);
			assertEquals("value" + i, map.get("key" + i));
		}

		commander.perform(ModifyMap.class, ModifyMap.Type.PUT, null, map,
				"key1", "other");
		assertEquals("other", map.get("key1"));

		commander.perform(Undo.class);
		assertEquals("value1", map.get("key1"));

		commander.perform(Redo.class);
		assertEquals("other", map.get("key1"));
		commander.perform(Undo.class);

		for (int i = 1; i <= 5; i++) {
			commander.perform(ModifyMap.class, ModifyMap.Type.REMOVE, null,
					map, "key" + i);
		}
		assertEquals(0, map.size());

		for (int i = 1; i <= 5; i++) {
			commander.perform(Undo.class);
		}
		for (int i = 1; i <= 5; i++) {
			assertEquals("value" + i, map.get("key" + i));
		}

	}

	@Test
	public void testSetMultipleField() {
		Entity e = new Entity();

		commander.perform(SetMultipleFields.class, e, "stringValue", "value1",
				"integerValue", 2, "floatValue", 4.0f);
		assertEquals(e.getStringValue(), "value1");
		assertEquals(e.integerValue, 2);
		assertEquals(e.floatValue, 4.0f, 0.0001f);

		// combine
		commander.perform(SetMultipleFields.class, e, "stringValue", "value2",
				"integerValue", 6, "floatValue", 8.0f, true);
		assertEquals(e.getStringValue(), "value2");
		assertEquals(e.integerValue, 6);
		assertEquals(e.floatValue, 8.0f, 0.0001f);
		commander.perform(SetMultipleFields.class, e, "stringValue", "value1",
				"integerValue", 2, "floatValue", 4.0f, true);
		assertEquals(e.getStringValue(), "value1");
		assertEquals(e.integerValue, 2);
		assertEquals(e.floatValue, 4.0f, 0.0001f);

		commander.perform(Undo.class);

		assertEquals(e.getStringValue(), "value1");
		assertEquals(e.integerValue, 2);
		assertEquals(e.floatValue, 4.0f, 0.0001f);
	}

	@Test
	public void testSetSelection() {
		Selection selection = commander.getSelection();

		commander.perform(SetSelection.class, null, "root", "Root object");
		commander
				.perform(SetSelection.class, "root", "parent", "Parent object");
		commander.perform(SetSelection.class, "parent", "child",
				"Child object 1", "Child object 2");

		assertEquals("Child object 1", selection.getCurrent()[0]);
		assertEquals("Child object 2", selection.getCurrent()[1]);

		assertEquals("Root object", selection.getSingle("root"));
		assertEquals("Parent object", selection.getSingle("parent"));
		assertNull(selection.getSingle("ñor"));

		commander.perform(Undo.class);

		assertEquals(0, selection.getContexts().size());
	}

	@Test
	public void testMultipleAction() {
		final Entity e = new Entity();
		e.setStringValue("a");
		e.integerValue = -1;
		commander.perform(TestMultipleAction.class, e);
		assertEquals(e.getStringValue(), "wow");
		assertEquals(e.integerValue, 15);
		commander.perform(Undo.class);
		assertEquals(e.getStringValue(), "a");
		assertEquals(e.integerValue, -1);
		commander.perform(Redo.class);
		assertEquals(e.getStringValue(), "wow");
		assertEquals(e.integerValue, 15);
	}

	@Test
	public void testSetSelectionHierarchy() {
		commander.perform(SetSelectionHierarchy.class, null, "context1", 1,
				"context2", 2, "context3", 3);
		for (int i = 1; i <= 3; i++) {
			assertEquals(i, commander.getSelection().getSingle("context" + i));
		}
	}

	public static class TestMultipleAction extends MultipleAction {

		@Override
		public void performActions(Object... args) {
			Entity e = (Entity) args[0];
			action(SetField.class, e, "integerValue", 15);
			action(SetField.class, e, "stringValue", "wow");
		}
	}

	@Test
	public void testSelectionAction() {
		Entity entity = new Entity();
		commander.perform(SetSelectionHierarchy.class, null, "entityContext",
				entity, "valueContext", "wow");
		commander.perform(SelectionAction.class, SetField.class,
				"entityContext", "stringValue", "valueContext");

		assertEquals(entity.getStringValue(), "wow");
	}
}
