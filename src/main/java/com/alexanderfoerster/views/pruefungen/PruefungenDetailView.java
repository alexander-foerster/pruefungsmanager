package com.alexanderfoerster.views.pruefungen;

import com.alexanderfoerster.commons.ExcelStreamResource;
import com.alexanderfoerster.commons.ReadExcelError;
import com.alexanderfoerster.data.Teilnehmer;
import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.services.PruefungService;
import com.alexanderfoerster.services.TeilnehmerService;
import com.alexanderfoerster.views.MainLayout;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@PageTitle("Prüfungsdetails")
@Route(value = "pruefungsdetails/:pruefungID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class PruefungenDetailView extends VerticalLayout implements BeforeEnterObserver {
    private PruefungService pruefungService;
    TeilnehmerService teilnehmerService;
    private final static String PRUEFUNG_ID = "pruefungID";
    private Optional<Pruefung> pruefungFromBackend = Optional.empty();
    private TextField id;
    private TextField bezeichnung;
    private TextField anzTeilnehmer;
    private DatePicker datum;
    private Grid<Teilnehmer> grid = new Grid<>(Teilnehmer.class);
    private StreamResource bewertungStreamResource;

    private final Button saveButton = new Button("Save changes");
    private final Button deleteButton = new Button("Delete entry");

    private final BeanValidationBinder<Pruefung> pruefungBinder;

    private Optional<Grid.Column<Teilnehmer>> currentColumn;
    private Optional<Teilnehmer> currentItem;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> pruefungsId = event.getRouteParameters().get(PRUEFUNG_ID).map(Long::parseLong);
        if(pruefungsId.isPresent()) {
            pruefungFromBackend = pruefungService.getWithTeilnehmers(pruefungsId.get());
            grid.setItems(pruefungFromBackend.get().getTeilnehmers());
        } else {
            //UI.getCurrent().navigate(PruefungenView.class);
            pruefungFromBackend = Optional.empty();
        }
        updateView();
    }

    private void updateView() {
        if(pruefungFromBackend.isPresent()) {
            id.setValue(Long.toString(pruefungFromBackend.get().getId()));
            pruefungBinder.readBean(pruefungFromBackend.get());
        } else {
            id.setValue("n.a.");
        }
    }

    public PruefungenDetailView(PruefungService pruefungService, TeilnehmerService teilnehmerService) {
        this.pruefungService = pruefungService;
        this.teilnehmerService = teilnehmerService;

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

        pruefungBinder = new BeanValidationBinder<>(Pruefung.class);

        pruefungBinder.forField(anzTeilnehmer)
                        .withConverter(new StringToIntegerConverter("Please enter a number"))
                                .bind("anzTeilnehmer");
        //binder.bind(bezeichnung, "bezeichnung");
        //binder.bind(datum, "datum");
        pruefungBinder.bindInstanceFields(this);

        saveButton.addClickListener(event -> {
            if(pruefungFromBackend.isPresent()) {
                try {
                    pruefungBinder.writeBean(this.pruefungFromBackend.get());
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

        HorizontalLayout crudButtons = new HorizontalLayout();
        crudButtons.add(saveButton, deleteButton);
        add(crudButtons);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        final var teilnehmerUpload = new Upload(memoryBuffer);

        Button uploadButton = new Button("Upload LSF XLS file...");

        teilnehmerUpload.setUploadButton(uploadButton);
        // Disable the upload button after the file is selected
        // Re-enable the upload button after the file is cleared
        uploadButton.getElement()
                .addEventListener("max-files-reached-changed", event -> {
                    boolean maxFilesReached = event.getEventData()
                            .getBoolean("event.detail.value");
                    uploadButton.setEnabled(!maxFilesReached);
                }).addEventData("event.detail.value");

        teilnehmerUpload.setAcceptedFileTypes(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel", ".xlsx", ".xls");

        teilnehmerUpload.addSucceededListener(succeededEvent -> {
            InputStream fileData = memoryBuffer.getInputStream();
            if(pruefungFromBackend.isPresent()) {
                try {
                    teilnehmerService.loadTeilnehmerFromXLS(pruefungFromBackend.get(), fileData);
//                    Notification.show("Anzahl Teilnehmer: " + anzTeilnehmer);
//                    pruefungFromBackend.get().setAnzTeilnehmer(anzTeilnehmer);
//                    pruefungService.update(pruefungFromBackend.get());
                    fileData.close();
                } catch (ReadExcelError e) {
                    Notification n = Notification.show("Fehler beim Laden: " + e.getFehlerMeldung());
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    n.setPosition(Notification.Position.MIDDLE);
                } catch (IOException e) {
                    Notification n = Notification.show("Fehler beim Laden: " + e.getLocalizedMessage());
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    n.setPosition(Notification.Position.MIDDLE);
                }

                // Reload Pruefung
                pruefungFromBackend = pruefungService.getWithTeilnehmers(pruefungFromBackend.get().getId());
                if(pruefungFromBackend.isPresent()) {
                    Pruefung pruefung = pruefungFromBackend.get();
                    int anzTeilnehmer = pruefungService.getAnzTeilnehmer(pruefungFromBackend);
                    pruefung.setAnzTeilnehmer(anzTeilnehmer);
                    pruefungService.update(pruefung);
                    pruefungBinder.readBean(pruefung);
                }
                grid.setItems(pruefungFromBackend.get().getTeilnehmers());
            }
        });
        teilnehmerUpload.addFileRejectedListener(fileRejectedEvent -> {
            Notification n = Notification.show("Datei konnte nicht geladen werden");
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            n.setPosition(Notification.Position.MIDDLE);
        });

        HorizontalLayout fileOperations = new HorizontalLayout();
        fileOperations.add(teilnehmerUpload);
        add(fileOperations);

        // Download area
        var downloadArea = new VerticalLayout();
        //bewertungStreamResource = exportExcel(teilnehmerService.saveBewertungstabelle(pruefungFromBackend));
        //Anchor link = new Anchor(bewertungStreamResource, String.format("Download Bewertungen.xlsx"));
        //link.getElement().setAttribute("download", true);
        Button exportButton = new Button("Export Bewertungs-Excel");
        exportButton.addClickListener(event -> exportExcel(teilnehmerService.saveBewertungstabelle(pruefungFromBackend)));

        downloadArea.add(exportButton);
        add(downloadArea);

        add(new H2("Teilnehmer"));
        grid.setColumns("matrNr", "nachname", "vorname");

        var binder = new BeanValidationBinder<>(Teilnehmer.class);
        var editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener(editorSaveEvent -> {
            Teilnehmer item = editorSaveEvent.getItem();
            teilnehmerService.save(item);
        });

        var colNote = grid.addColumn("note");
        var txtNote = new TextField();
        binder.forField(txtNote)
                .withConverter(new StringToDoubleConverter("Please enter a number"))
                .bind("note");
        colNote.setEditorComponent(txtNote);


        grid.addCellFocusListener(event -> {
            // Store the item on cell focus. Used in the ENTER ShortcutListener
            currentItem = event.getItem();
            // Store the current column. Used in the SelectionListener to focus the editor component
            currentColumn = event.getColumn();
        });

        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(teilnehmer -> {
            editor.save();
            if (!editor.isOpen()) {
                grid.getEditor().editItem(teilnehmer);
                currentColumn.ifPresent(column -> {
                    if (column.getEditorComponent() instanceof Focusable<?> focusable) {
                        focusable.focus();
                    }
                });
            }
        }));

        Shortcuts.addShortcutListener(grid, event -> currentItem.ifPresent(grid::select), Key.ENTER).listenOn(grid);
        Shortcuts.addShortcutListener(grid, () -> {
            if (editor.isOpen()) {
                editor.cancel();
            }
        }, Key.ESCAPE).listenOn(grid);

        grid.addColumn(new ComponentRenderer<>(teilnehmer -> {
           Checkbox teilnehmerCB = new Checkbox();
           teilnehmerCB.setValue(teilnehmer.isBewertet());
           teilnehmerCB.addValueChangeListener(event -> {
              teilnehmer.setBewertet(event.getValue());
              teilnehmerService.save(teilnehmer);
           });
           return teilnehmerCB;
        })).setHeader("Bewertet").setKey("bewertet");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        add(grid);
    }

    private void exportExcel(XSSFWorkbook saveBewertungstabelle) {
        ExcelStreamResource resource = new ExcelStreamResource(saveBewertungstabelle, "Bewertungen.xlsx");
        resource.setCacheTime(0); // Disable cache

        Anchor anchor = new Anchor(resource, "Download Excel");
        anchor.getElement().setAttribute("download", true);
        anchor.getElement().getStyle().set("display", "none"); // Hide the anchor
        UI.getCurrent().add(anchor);
        anchor.getElement().executeJs("this.click()");
    }
}
