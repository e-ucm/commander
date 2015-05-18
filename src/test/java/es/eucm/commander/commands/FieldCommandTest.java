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

import es.eucm.commander.Entity;
import es.eucm.commander.SubEntity;
import es.eucm.commander.events.FieldEvent;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FieldCommandTest {

	@Test
	public void testBasic() {
		Entity e = new Entity();
		e.floatValue = 12.0f;
		e.integerValue = 47;
		e.setStringValue("ñor");
		List<String> list1 = (List<String>) (e.objectValue = Arrays.asList("a",
				"b"));

		FieldCommand command1 = new FieldCommand(e, "floatValue", 32.0f);
		FieldCommand command2 = new FieldCommand(e, "integerValue", 23);
		FieldCommand command3 = new FieldCommand(e, "stringValue", "hi");
		FieldCommand command4 = new FieldCommand(e, "objectValue", null);

		FieldEvent event1 = command1.doCommand();
		FieldEvent event2 = command2.doCommand();
		FieldEvent event3 = command3.doCommand();
		FieldEvent event4 = command4.doCommand();

		assertEquals(e.floatValue, 32.0f, 0.00001f);
		assertEquals(e.integerValue, 23);
		assertEquals(e.getStringValue(), "hi");
		assertEquals(e.objectValue, null);
		assertEquals(e.floatValue, ((Float) event1.getValue()), 0.00001f);
		assertEquals(e.integerValue, ((Integer) event2.getValue()).intValue());
		assertEquals(e.getStringValue(), event3.getValue());
		assertEquals(e.objectValue, event4.getValue());

		event1 = command1.undoCommand();
		event2 = command2.undoCommand();
		event3 = command3.undoCommand();
		event4 = command4.undoCommand();

		assertEquals(e.floatValue, 12.0f, 0.00001f);
		assertEquals(e.integerValue, 47);
		assertEquals(e.getStringValue(), "ñor");
		assertEquals(e.objectValue, list1);
		assertEquals(e.floatValue, ((Float) event1.getValue()), 0.00001f);
		assertEquals(e.integerValue, ((Integer) event2.getValue()).intValue());
		assertEquals(e.getStringValue(), event3.getValue());
		assertEquals(e.objectValue, event4.getValue());
	}

	@Test
	public void testFieldFromSuperClass() {
		SubEntity e = new SubEntity();
		e.floatValue = 12.0f;
		new FieldCommand(e, "floatValue", 32.0f).doCommand();
	}

	@Test
	public void testCombine() {
		Entity e = new Entity();
		e.floatValue = 12.0f;
		FieldCommand command1 = new FieldCommand(e, "floatValue", 32.0f, true);
		FieldCommand command2 = new FieldCommand(e, "floatValue", 27.0f);
		assertTrue(command1.combine(command2));

		command1.doCommand();
		assertEquals(e.floatValue, 27.0f, 0.001f);
	}

	@Test
	public void testInvalidArguments() {
		Entity e = new Entity();
		assertNull(new FieldCommand(e, "floatValue", "invalidType").doCommand());
	}

}
