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
import es.eucm.commander.actions.AddResource;
import es.eucm.commander.actions.RemoveResource;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ModelTest {

	@Test
	public void testCommit() {
		Commander commander = new Commander();
		Model model = commander.getModel();
		TestCommitter committer = new TestCommitter();
		model.setCommitter(committer);

		for (int i = 0; i < 3; i++) {
			commander.perform(AddResource.class, "Entity", "entity" + i,
					new Entity());
		}

		for (int i = 0; i < 3; i++) {
			assertEquals(State.MODIFIED, model.getResource("entity" + i)
					.getState());
		}

		for (int i = 0; i < 3; i++) {
			committer.modified.add("entity" + i);
		}
		model.commit();

		assertEquals(0, committer.modified.size());

		for (int i = 0; i < 3; i++) {
			assertEquals(State.COMMITTED, model.getResource("entity" + i)
					.getState());
		}

		commander.perform(RemoveResource.class, "entity1");
		assertEquals(2, model.getCategoryResources("Entity").size());
		committer.deleted.add("entity1");
		model.commit();
		assertEquals(0, committer.deleted.size());
		assertEquals(2, model.getCategoryResources("Entity").size());
	}

	public static class TestCommitter implements Committer {

		ArrayList<String> modified = new ArrayList<String>();

		ArrayList<String> deleted = new ArrayList<String>();

		@Override
		public void commitModified(Resource resource) {
			if (!modified.contains(resource.getId())) {
				fail();
			}
			modified.remove(resource.getId());
		}

		@Override
		public void commitRemoved(Resource resource) {
			if (!deleted.contains(resource.getId())) {
				fail();
			}
			deleted.remove(resource.getId());
		}
	}
}
