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

import es.eucm.commander.Commander;
import es.eucm.commander.actions.AddResource;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TextNotesDemo {

	public static void main(String args[]) {
		final Commander commander = new Commander();

		JFrame frame = new TextNotesFrame(commander);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (File file : TextComitter.storeFolder.listFiles()) {
					try {
						String line;
						BufferedReader reader = new BufferedReader(
								new FileReader(file));
						int i = 0;
						String id = file.getName();
						TextNote note = new TextNote(id);
						while ((line = reader.readLine()) != null) {
							switch (i) {
							case 0:
								note.setTitle(line);
								break;
							case 1:
								note.setText(line);
								break;
							case 2:
								note.setPriority(Integer.parseInt(line));
								break;
							}
							i++;
						}
						commander.perform(AddResource.class, "text", id, note);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
