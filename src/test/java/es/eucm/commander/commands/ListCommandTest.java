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
import es.eucm.commander.actions.ModifyList.LWrapperFactory.LWrapper;
import es.eucm.commander.commands.ListCommand.AddToListCommand;
import es.eucm.commander.commands.ListCommand.RemoveFromListCommand;
import es.eucm.commander.commands.ListCommand.ReorderInListCommand;
import es.eucm.commander.events.ListEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListCommandTest {

	private LWrapper list;

	@Before
	public void setUp() {
		list = new LWrapper(new ArrayList<Entity>());
	}

	@Test
	public void testAdd() {
		Entity entity = new Entity();
		AddToListCommand command = new AddToListCommand(null, list, entity);
		ListEvent event = command.doCommand();
		assertEquals(list.get(0), entity);
		// Check also event was formed as expected
		testListEvent(event, entity, 0, ListEvent.Type.ADDED);

		ListEvent event2 = (ListEvent) command.undoCommand();
		assertTrue(list.size() == 0);
		// Check also event was formed as expected
		testListEvent(event2, entity, 0, ListEvent.Type.REMOVED);
	}

	@Test
	public void testAddSpecificIndex() {
		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);

		Entity newEntity = new Entity();

		AddToListCommand command = new AddToListCommand(null, list, newEntity,
				1);
		ListEvent event = command.doCommand();
		assertEquals(list.get(0), entity1);
		assertEquals(list.get(1), newEntity);
		assertEquals(list.get(2), entity2);
		assertEquals(list.get(3), entity3);
		// Check also event was formed as expected
		testListEvent(event, newEntity, 1, ListEvent.Type.ADDED);
	}

	@Test
	public void testRemove() {
		Entity entity = new Entity();
		list.add(entity);
		RemoveFromListCommand command = new RemoveFromListCommand(null, list,
				entity);
		ListEvent event = command.doCommand();
		assertTrue(list.size() == 0);
		testListEvent(event, entity, 0, ListEvent.Type.REMOVED);

		ListEvent event2 = (ListEvent) command.undoCommand();
		assertEquals(entity, list.get(0));
		testListEvent(event2, entity, 0, ListEvent.Type.ADDED);
	}

	@Test
	public void testRemoveNonExistingItem() {
		Entity entity = new Entity();
		Entity entity1 = new Entity();
		list.add(entity);
		RemoveFromListCommand command = new RemoveFromListCommand(null, list,
				entity1);
		command.doCommand();
		assertEquals(list.get(0), entity);
		command.undoCommand();
		assertEquals(list.get(0), entity);
	}

	@Test
	public void testReorderList() {
		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		Entity entity3 = new Entity();
		Entity entity4 = new Entity();
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		list.add(entity4);

		ReorderInListCommand command = new ReorderInListCommand(null, list,
				entity4, 0);
		command.doCommand();
		assertEquals(list.indexOf(entity4), 0);
		command.undoCommand();
		assertEquals(list.indexOf(entity4), 3);

		ReorderInListCommand command2 = new ReorderInListCommand(null, list,
				entity1, 2);
		command2.doCommand();
		assertEquals(list.indexOf(entity1), 2);
		command2.undoCommand();
		assertEquals(list.indexOf(entity1), 0);
	}

	private void testListEvent(ListEvent event, Entity expectedElement,
			int expectedIndex, ListEvent.Type expectedType) {
		assertEquals(list.getList(), event.getTarget());
		assertEquals(expectedElement, event.getElement());
		assertEquals(expectedIndex, event.getIndex());
		assertEquals(expectedType, event.getType());
	}
}
