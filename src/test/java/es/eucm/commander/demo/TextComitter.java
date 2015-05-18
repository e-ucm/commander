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
package es.eucm.commander.demo;

import es.eucm.commander.Committer;
import es.eucm.commander.Resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextComitter implements Committer {

	public static File storeFolder = new File("notes");

	static {
		if (!storeFolder.exists()) {
			storeFolder.mkdirs();
		}
	}

	@Override
	public void commitModified(Resource resource) {
		TextNote note = (TextNote) resource.getObject();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					storeFolder, note.getId())));
			writer.write(note.getTitle());
			writer.newLine();
			writer.write(note.getText());
			writer.newLine();
			writer.write(note.getPriority() + "");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commitRemoved(Resource resource) {
		File file = new File(storeFolder, resource.getId());
		if (file.exists()) {
			file.delete();
		}
	}
}
