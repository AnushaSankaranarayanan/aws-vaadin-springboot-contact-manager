package com.example.vaadin.ui.views.list;

import com.example.vaadin.backend.entity.Company;
import com.example.vaadin.backend.entity.Contact;
import com.example.vaadin.backend.service.CompanyService;
import com.example.vaadin.backend.service.ContactService;
import com.example.vaadin.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Contact Manager")
public class ListView extends VerticalLayout {
    Grid<Contact> contactGrid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactService contactService;
    ContactForm contactForm;

    public ListView(ContactService contactService, CompanyService companyService) {
        this.contactService = contactService;
        contactForm = new ContactForm(companyService.findAll());
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        contactForm.addListener(ContactForm.SaveEvent.class, this::saveContact);
        contactForm.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        contactForm.addListener(ContactForm.CloseEvent.class, this::closeContact);

        Div content = new Div(contactGrid, contactForm);
        content.addClassName("content");
        content.setSizeFull();
        add(getToolBar(), content);
        updateContactList();
        closeEditor();
    }


    private void saveContact(ContactForm.SaveEvent evt) {
        contactService.save(evt.getContact());
        updateContactList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent evt) {
        contactService.delete(evt.getContact());
        updateContactList();
        closeEditor();
    }

    private void closeContact(ContactForm.CloseEvent evt) {
        closeEditor();
    }

    private void configureGrid() {
        contactGrid.addClassName("contact-grid");
        contactGrid.setSizeFull();
        contactGrid.setColumns("firstName", "lastName", "email", "status");
        contactGrid.addColumn(contact -> getCompanyOfContact(contact.getCompany()))
                .setHeader("Company");
        contactGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        contactGrid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateContactList());

        Button addContactBtn = new Button("Add Contact", (click -> addContact()));
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addContact() {
        contactGrid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void updateContactList() {
        contactGrid.setItems(contactService.findAll(filterText.getValue()));
    }

    private String getCompanyOfContact(Company company) {
        return company == null ? "---" : company.getName();
    }

    private void editContact(Contact contact) {
        if (contact == null) {
            closeEditor();
        } else {
            contactForm.setContact(contact);
            contactForm.setVisible(true);
            contactForm.addClassName("editing");
        }

    }

    private void closeEditor() {
        contactForm.setContact(null);
        contactForm.setVisible(false);
        removeClassName("editing");
    }

}
