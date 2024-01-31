package com.alexanderfoerster.views.pruefungen;

import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.services.PruefungService;
import com.alexanderfoerster.views.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@PageTitle("Prüfungsdetails")
@Route(value = "pruefungsdetails/:pruefungID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class PruefungenDetailView extends VerticalLayout implements BeforeEnterObserver {
    private PruefungService pruefungService;
    private final static String PRUEFUNG_ID = "pruefungID";
    private Optional<Pruefung> pruefungFromBackend = Optional.empty();
    private final TextField pruefungsIdTF;

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
            pruefungsIdTF.setValue(Long.toString(pruefungFromBackend.get().getId()));
        } else {
            pruefungsIdTF.setValue("n.a.");
        }
    }

    public PruefungenDetailView(PruefungService pruefungService) {
        this.pruefungService = pruefungService;

        add(new H1("Prüfungsdetails"));

        pruefungsIdTF = new TextField("Prüfungs-ID");
        pruefungsIdTF.setEnabled(false);
        pruefungsIdTF.setValue("---");
        add(pruefungsIdTF);
    }
}
