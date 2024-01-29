package com.alexanderfoerster.views.helloworld;

import com.alexanderfoerster.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HelloWorldView extends VerticalLayout {

    private final DatePicker startDateDP;
    private final TextField weeksTF;
    private final TextField resultDateTF;
    private final Button calcButton;

    public HelloWorldView() {
        final var headline = new H1("Prüfungsmanager - Allgemeine Tools");

        final var dateCalculatorSection = new HorizontalLayout();

        startDateDP = new DatePicker("Start Date");

        weeksTF = new TextField("Weeks");

        resultDateTF = new TextField("Result");
        resultDateTF.setEnabled(false);

        calcButton = new Button("Add weeks");
        calcButton.addClickListener(e -> {
            final var startDate = startDateDP.getValue();

            final var weeksString = weeksTF.getValue();
            try {
                final var weeks = Long.parseLong(weeksString);
                final var resultDate = startDate.plusWeeks(weeks);
                final var localMediumDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                resultDateTF.setValue(localMediumDate.format(resultDate));
            } catch (NumberFormatException nfe) {
                Notification.show("Wochenanzahl nicht korrekt");
            }
        });
        calcButton.addClickShortcut(Key.ENTER);

        dateCalculatorSection.setMargin(true);
        dateCalculatorSection.setVerticalComponentAlignment(Alignment.END, startDateDP, weeksTF, calcButton, resultDateTF);

        dateCalculatorSection.add(startDateDP, weeksTF, calcButton, resultDateTF);

        this.setMargin(true);
        add(headline);
        add(dateCalculatorSection);
    }

}
