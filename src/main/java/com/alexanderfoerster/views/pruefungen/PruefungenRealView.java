package com.alexanderfoerster.views.pruefungen;

import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.services.PruefungService;
import com.alexanderfoerster.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;

@PageTitle("Pruefungen Real")
@Route(value = "pruefungenreal/:pruefungsID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class PruefungenRealView extends VerticalLayout {
    private PruefungService pruefungService;
    private Grid<Pruefung> grid = new Grid<>(Pruefung.class);
    private final static String PRUEFUNG_EDIT_ROUTE_TEMPLATE = "pruefungsdetails/%s/edit";

    public PruefungenRealView(PruefungService pruefungService) {
        this.pruefungService = pruefungService;

        add(new H1("Prüfungen managen"));

        final var neuePruefungButton = new Button("Neue Prüfung");
        neuePruefungButton.addClickListener( buttonClickEvent -> {
           pruefungService.update(new Pruefung(
                LocalDate.now(), 0, "Neue Prüfung"
           ));
           refreshGrid();
        });
        add(neuePruefungButton);

        grid.setColumns("datum", "bezeichnung", "anzTeilnehmer");
        grid.addColumn(Pruefung::getAbgabeZeit).setHeader("Abgabe");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Long id = event.getValue().getId();
                if(id != null)
                    UI.getCurrent().navigate(String.format(PRUEFUNG_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                //Notification n = Notification.show("inkorrekte ID");
                //n.setPosition(Notification.Position.MIDDLE);
                //n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                UI.getCurrent().navigate(PruefungenRealView.class);
            }
        });

        refreshGrid();
        add(grid);
    }

    private void refreshGrid() {
        // grid.select(null);
        // Dies läd wohl keine neuen Zeilen nach
        //grid.getDataProvider().refreshAll();
        grid.setItems(pruefungService.listAll());
    }
}
