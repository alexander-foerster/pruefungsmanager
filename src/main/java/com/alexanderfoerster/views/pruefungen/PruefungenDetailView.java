package com.alexanderfoerster.views.pruefungen;

import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.services.PruefungService;
import com.alexanderfoerster.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@PageTitle("Prüfungsdetails")
@Route(value = "pruefungsdetails/:pruefungID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class PruefungenDetailView extends VerticalLayout implements BeforeEnterObserver {
    private PruefungService pruefungService;
    private final static String PRUEFUNG_ID = "pruefungID";
    private Optional<Pruefung> pruefungFromBackend = Optional.empty();
    private TextField id;
    private TextField bezeichnung;
    private TextField anzTeilnehmer;
    private DatePicker datum;

    private final Button saveButton = new Button("Save changes");
    private final Button deleteButton = new Button("Delete entry");

    private final BeanValidationBinder<Pruefung> binder;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> pruefungsId = event.getRouteParameters().get(PRUEFUNG_ID).map(Long::parseLong);
        if(pruefungsId.isPresent()) {
            pruefungFromBackend = pruefungService.get(pruefungsId.get());
        } else {
            //UI.getCurrent().navigate(PruefungenView.class);
            pruefungFromBackend = Optional.empty();
        }
        updateView();
    }

    private void updateView() {
        if(pruefungFromBackend.isPresent()) {
            id.setValue(Long.toString(pruefungFromBackend.get().getId()));
            binder.readBean(pruefungFromBackend.get());
        } else {
            id.setValue("n.a.");
        }
    }

    public PruefungenDetailView(PruefungService pruefungService) {
        this.pruefungService = pruefungService;

        add(new H1("Prüfungsdetails"));

        FormLayout formLayout = new FormLayout();
        this.id = new TextField("Prüfungs-ID");
        this.id.setEnabled(false);
        this.datum = new DatePicker("Datum");
        this.bezeichnung = new TextField("Bezeichnung");
        this.anzTeilnehmer = new TextField("Anzahl Teilnehmer");
        anzTeilnehmer.setEnabled(false);
        formLayout.add(id, datum, bezeichnung, anzTeilnehmer);

        add(formLayout);

        binder = new BeanValidationBinder<>(Pruefung.class);

        binder.forField(anzTeilnehmer)
                        .withConverter(new StringToIntegerConverter("Please enter a number"))
                                .bind("anzTeilnehmer");
        //binder.bind(bezeichnung, "bezeichnung");
        //binder.bind(datum, "datum");
        binder.bindInstanceFields(this);

        saveButton.addClickListener(event -> {
            if(pruefungFromBackend.isPresent()) {
                try {
                    binder.writeBean(this.pruefungFromBackend.get());
                    pruefungService.update(this.pruefungFromBackend.get());
                } catch (ValidationException e) {
                    Notification n = Notification.show("Validation error");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (ObjectOptimisticLockingFailureException exception) {
                    Notification n = Notification.show(
                            "Error updating the data. Somebody else has updated the record while you were making changes.");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                UI.getCurrent().navigate(PruefungenRealView.class);
            }
        });


        deleteButton.addClickListener( event -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Delete?");
            confirmDialog.setText("Are you shure to delete this entry?");
            confirmDialog.setCancelable(true);
            confirmDialog.setConfirmText("Delete");
            confirmDialog.setConfirmButtonTheme("error primary");
            confirmDialog.addCancelListener(cancelEvent -> {
               confirmDialog.close();
            });
            confirmDialog.addConfirmListener(confirmEvent -> {
                if(pruefungFromBackend.isPresent()) {
                    try {
                        pruefungService.delete(this.pruefungFromBackend.get().getId());
                    } catch (ObjectOptimisticLockingFailureException exception) {
                        Notification n = Notification.show(
                                "Error updating the data. Somebody else has updated the record while you were making changes.");
                        n.setPosition(Notification.Position.MIDDLE);
                        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                    UI.getCurrent().navigate(PruefungenRealView.class);
                }
            });
            confirmDialog.open();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout h = new HorizontalLayout();
        h.add(saveButton, deleteButton);
        add(h);
    }
}
