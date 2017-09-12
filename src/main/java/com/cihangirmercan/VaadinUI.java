package com.cihangirmercan;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@SpringUI
public class VaadinUI extends UI {

	private final PersonRepository repo;

	private final PersonEditor editor;

	final Grid<Person> grid;

	final TextField filter;

	private final Button addNewBtn;

	@Autowired
	public VaadinUI(PersonRepository repo, PersonEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Person.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New person");
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
		setContent(mainLayout);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "name", "email");

		filter.setPlaceholder("Filter by name");

		// Hook logic to components

		// Replace listing with filtered content when person changes filter
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> listPeople(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editPerson(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editPerson(new Person("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listPeople(filter.getValue());
		});

		// Initialize listing
		listPeople(null);
	}

	// tag::listCustomers[]
	void listPeople(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems((Collection<Person>) repo.findAll());
		} else {
			grid.setItems(repo.findByNameStartsWithIgnoreCase(filterText));
		}
	}
}