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

import es.eucm.commander.Model.FieldListener;
import es.eucm.commander.events.FieldEvent;

import javax.swing.JSpinner;
import javax.swing.JTextField;

public class TextFieldListener implements FieldListener {

	JTextField title;

	JTextField text;

	JSpinner priority;

	public TextFieldListener(JTextField title, JTextField text,
			JSpinner priority) {
		this.title = title;
		this.text = text;
		this.priority = priority;
	}

	@Override
	public boolean listenToField(String fieldName) {
		return true;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		if ("title".equals(event.getField())) {
			if (!event.getValue().equals(title.getText())) {
				title.setText((String) event.getValue());
			}
		} else if ("text".equals(event.getField())) {
			if (!event.getValue().equals(text.getText())) {
				text.setText((String) event.getValue());
			}
		} else if ("priority".equals(event.getField())) {
			priority.setValue(event.getValue());
		}

	}
}
