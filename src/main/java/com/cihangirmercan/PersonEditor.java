package com.cihangirmercan;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * A simple example to introduce building forms. As your real application is probably much
 * more complicated than this example, you could re-use this form in multiple places. This
 * example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Viritin
 * (https://vaadin.com/addon/viritin).
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class PersonEditor extends VerticalLayout {

	private final PersonRepository repository;

	/**
	 * The currently edited person
	 */
	private Person person;

	/* Fields to edit properties in person entity */
	TextField name = new TextField("name");
	TextField email = new TextField("email");

	/* Action buttons */
	Button save = new Button("Save");
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete");
	CssLayout actions = new CssLayout(save, cancel, delete);

	Binder<Person> binder = new Binder<>(Person.class);

	@Autowired
	public PersonEditor(PersonRepository repository) {
		this.repository = repository;

		addComponents(name, email, actions);

		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> repository.save(person));
		delete.addClickListener(e -> repository.delete(person));
		cancel.addClickListener(e -> setVisible(false));
		setVisible(false);
	}


	public final void editPerson(Person p) {
		if (p == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = p.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			person = repository.findOne(p.getId());
		}
		else {
			person = p;
		}
		delete.setVisible(persisted);
		
		// Bind person properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(person);

		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		name.selectAll();
	}

	public interface ChangeHandler {

		void onChange();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
	}

}