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
import es.eucm.commander.Model;
import es.eucm.commander.Resource;
import es.eucm.commander.actions.Action;
import es.eucm.commander.actions.AddResource;
import es.eucm.commander.actions.Commit;
import es.eucm.commander.actions.Redo;
import es.eucm.commander.actions.RemoveResource;
import es.eucm.commander.actions.SetField;
import es.eucm.commander.actions.SetRootSelection;
import es.eucm.commander.actions.SetSelection;
import es.eucm.commander.actions.Undo;
import es.eucm.commander.demo.listeners.ResourceListener;
import es.eucm.commander.demo.listeners.SelectionListener;
import es.eucm.commander.events.ResourceEvent;
import es.eucm.commander.events.SelectionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextNotesFrame extends JFrame {

	private Commander commander;

	private JList resources;
	private JSpinner priority;
	private JPanel editionPanel;
	private JTextField title;
	private JTextField text;
	private JButton removeNote;

	public TextNotesFrame(Commander commander) {
		super("Text notes");
		this.commander = commander;
		commander.getModel().setCommitter(new TextComitter());

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(buildTools(), BorderLayout.PAGE_START);
		container.add(buildResourcesList(), BorderLayout.LINE_START);
		container.add(buildEditor(), BorderLayout.CENTER);

		commander.getModel().addListener(
				ResourceEvent.class,
				new ResourceListener((DefaultListModel<TextNote>) resources
						.getModel()));

		commander.getModel().addListener(
				SelectionEvent.class,
				new SelectionListener(commander, removeNote, resources,
						editionPanel, title, text, priority));

	}

	public JPanel buildTools() {
		JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));

		// Undo
		final JButton undo = new JButton("Undo");
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				commander.perform(Undo.class);
			}
		});

		undo.setEnabled(commander.isEnabled(Undo.class));
		commander.addActionListener(Undo.class, new Action.ActionListener() {
			@Override
			public void enableUpdated(Class actionClass, boolean enabled) {
				undo.setEnabled(enabled);
			}
		});
		tools.add(undo);

		// Redo
		final JButton redo = new JButton("Redo");
		redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				commander.perform(Redo.class);
			}
		});

		redo.setEnabled(commander.isEnabled(Redo.class));
		commander.addActionListener(Redo.class, new Action.ActionListener() {
			@Override
			public void enableUpdated(Class actionClass, boolean enabled) {
				redo.setEnabled(enabled);
			}
		});
		tools.add(redo);

		// Save
		final JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				commander.perform(Commit.class);
			}
		});

		save.setEnabled(commander.isEnabled(Commit.class));
		commander.addActionListener(Commit.class, new Action.ActionListener() {
			@Override
			public void enableUpdated(Class actionClass, boolean enabled) {
				save.setEnabled(enabled);
			}
		});
		tools.add(save);

		return tools;
	}

	public JPanel buildResourcesList() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		JPanel tools = new JPanel();
		JButton addNote = new JButton("Add");
		addNote.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String id = commander.getModel().createId(TextNote.CATEGORY);
				TextNote note = new TextNote(id);
				commander.perform(AddResource.class, TextNote.CATEGORY, id,
						note);
				commander.perform(SetRootSelection.class, id);
				commander.perform(SetSelection.class, Model.RESOURCE,
						"content", note);
			}
		});

		removeNote = new JButton("Remove");
		removeNote.setEnabled(false);
		removeNote.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String id = ((Resource) commander.getSelection().getSingle(
						Model.RESOURCE)).getId();
				commander.perform(RemoveResource.class, id);
				commander.perform(SetRootSelection.class);
			}
		});

		tools.add(addNote);
		tools.add(removeNote);

		c.gridx = 0;
		c.gridy = 0;
		c.fill = 1;
		panel.add(tools, c);

		resources = new JList<TextNote>(new DefaultListModel<TextNote>());
		resources.setPreferredSize(new Dimension(20, 500));
		resources.setSize(400, 600);

		resources.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				TextNote textNote = (TextNote) resources.getSelectedValue();
				Resource resource = ((Resource) commander.getSelection()
						.getSingle(Model.RESOURCE));
				if (resource == null
						|| (textNote != null && !textNote.getId().equals(
								resource.getId()))) {
					commander.perform(SetRootSelection.class, textNote.getId());
					commander.perform(SetSelection.class, Model.RESOURCE,
							"content", textNote);
				}
			}
		});

		c.gridy = 1;
		panel.add(resources, c);
		return panel;
	}

	public JPanel buildEditor() {
		editionPanel = new JPanel();
		editionPanel.setLayout(new GridLayout(3, 1));

		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.add(new JLabel("Title: "));
		title = new JTextField("");
		title.setPreferredSize(new Dimension(200, 20));
		title.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				TextNote note = (TextNote) commander.getSelection().getSingle(
						"content");
				commander.perform(SetField.class, note, "title",
						title.getText(), true);
			}
		});

		titlePanel.add(title);
		editionPanel.add(titlePanel);

		JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		textPanel.add(new JLabel("Text: "));
		text = new JTextField("");
		text.setPreferredSize(new Dimension(200, 20));
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				TextNote note = (TextNote) commander.getSelection().getSingle(
						"content");
				commander.perform(SetField.class, note, "text", text.getText(),
						true);
			}
		});
		textPanel.add(text);
		editionPanel.add(textPanel);

		JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		priorityPanel.add(new JLabel("Priority: "));
		priority = new JSpinner();
		priority.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				TextNote note = (TextNote) commander.getSelection().getSingle(
						"content");

				if (!priority.getValue().equals(note.getPriority())) {
					commander.perform(SetField.class, note, "priority",
							priority.getValue());
				}
			}
		});
		priorityPanel.add(priority);
		editionPanel.add(priorityPanel);
		editionPanel.setVisible(false);

		return editionPanel;
	}

}
