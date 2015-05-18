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
package es.eucm.commander.demo.listeners;

import es.eucm.commander.Commander;
import es.eucm.commander.Model;
import es.eucm.commander.Model.ModelListener;
import es.eucm.commander.Resource;
import es.eucm.commander.demo.TextNote;
import es.eucm.commander.events.FieldEvent;
import es.eucm.commander.events.SelectionEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListModel;

public class SelectionListener implements ModelListener<SelectionEvent> {

	private Commander commander;

	private JButton removeResource;

	private JList<TextNote> resources;

	private JPanel notePanel;

	private JTextField title;

	private JTextField text;

	private JSpinner priority;

	private TextFieldListener fieldListener;

	public SelectionListener(Commander commander, JButton removeResource,
			JList<TextNote> resources, JPanel notePanel, JTextField title,
			JTextField text, JSpinner priority) {
		this.commander = commander;
		this.removeResource = removeResource;
		this.resources = resources;
		this.notePanel = notePanel;
		this.title = title;
		this.text = text;
		this.priority = priority;
		this.fieldListener = new TextFieldListener(title, text, priority);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		if (Model.RESOURCE.equals(event.getContextId())) {
			switch (event.getType()) {
			case ADDED:
				if (event.getSelection().length > 0) {
					removeResource.setEnabled(true);
					ListModel<TextNote> notes = resources.getModel();
					for (int i = 0; i < notes.getSize(); i++) {
						TextNote n = notes.getElementAt(i);
						if (n.getId().equals(
								((Resource) event.getSelection()[0]).getId())) {
							resources.setSelectedIndex(i);
							return;
						}
					}
				}
				break;
			case REMOVED:
				removeResource.setEnabled(false);
				notePanel.setVisible(false);
				resources.clearSelection();
				break;
			}

		} else if ("content".equals(event.getContextId())) {
			switch (event.getType()) {
			case FOCUSED:
				notePanel.setVisible(true);
				TextNote note = (TextNote) event.getSelection()[0];
				title.setText(note.getTitle());
				text.setText(note.getText());
				priority.setValue(note.getPriority());
				commander.getModel().addListener(note, FieldEvent.class,
						fieldListener);
				break;
			case REMOVED:
				commander.getModel().removeListener(fieldListener);
				break;
			}
		}
	}
}
