package com.alexanderfoerster.services;

import com.alexanderfoerster.commons.ExcelHelper;
import com.alexanderfoerster.commons.ReadExcelError;
import com.alexanderfoerster.data.Pruefung;
import com.alexanderfoerster.data.Teilnehmer;
import com.alexanderfoerster.data.TeilnehmerRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TeilnehmerService {

    private TeilnehmerRepository teilnehmerRepository;

    public TeilnehmerService(TeilnehmerRepository teilnehmerRepository) {
        this.teilnehmerRepository = teilnehmerRepository;
    }

    private final String ANFANGS_MARKER = "startHISsheet";
    private final String END_MARKER = "endHISsheet";

    public XSSFWorkbook saveBewertungstabelle(Optional<Pruefung> pruefungFromUI) {
        final var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Bewertungen");
        var titleRow = sheet.createRow(0);
        var cell = titleRow.createCell(0);

        Pruefung pruefung;
        if(pruefungFromUI.isEmpty()) {
            cell.setCellValue("Keine Prüfung geladen");
            return workbook;
        } else
            pruefung = pruefungFromUI.get();

        cell.setCellValue("Bewertungen" + pruefung.getBezeichnung());

        var rowNr = 2;
        var teilnehmerListe = teilnehmerRepository.findAllByPruefungOrderByNachname(pruefung);
        for(var teilnehmer : teilnehmerListe)  {
            var row = sheet.createRow(rowNr);
            var matrCell = row.createCell(0);
            matrCell.setCellValue(teilnehmer.getMatrNr());
            var nachnameCell = row.createCell(1);
            nachnameCell.setCellValue(teilnehmer.getNachname());
            var vornameCell = row.createCell(2);
            vornameCell.setCellValue(teilnehmer.getVorname());
            ++rowNr;
        }
        return workbook;
    }

    public void loadTeilnehmerFromXLS(Pruefung pruefung, InputStream inputStream) throws ReadExcelError, IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheet("First Sheet");
        if(sheet == null)
            throw new ReadExcelError("First Sheet 'Paare' nicht gefunden");

        // Teste, ob die Excel-Datei die korrekten Header hat
        String startMarker = ExcelHelper.readTextCell(sheet, 2, 0);
        if(startMarker.compareTo(ANFANGS_MARKER) != 0)
            throw new ReadExcelError("Zeile 3 hat keinen Anfangmarker");

        // Bisherige Teilnehmer-Einträge löschen
        List<Teilnehmer> bisherigeTeilnehmer = teilnehmerRepository.findAllByPruefungOrderByNachname(pruefung);
        for(var tln : bisherigeTeilnehmer)
            teilnehmerRepository.delete(tln);

        // Zeilenweise einlesen, bis eine Zeile den Endmarker enthält
        int rowNr = 4;
        while(true) {
            int matrNr;

            String firstColumn = ExcelHelper.readTextCell(sheet, rowNr, 0);
            if (firstColumn.equals(END_MARKER))
                break;

            try {
                matrNr = Integer.parseInt(firstColumn);
            } catch (NumberFormatException nfe) {
                throw new ReadExcelError("Fehler bei Matrikelnummer in Zeile " + (rowNr + 1));
            }

            String name = ExcelHelper.readTextCell(sheet, rowNr, 5);
            String[] namensTeile = name.split(",");
            if (namensTeile.length != 2)
                throw new ReadExcelError("Falscher Name in Spalte F in Zeile " + (rowNr + 1));
            String nachname = namensTeile[0];
            String vorname = namensTeile[1];

            double note = 0.0;
            String bewertung = ExcelHelper.readTextCellOrBlank(sheet, rowNr, 6).trim();
            if (bewertung.length() > 0) {
                try {
                    double bewertungDouble = Double.parseDouble(bewertung);
                    note = bewertungDouble / 100.0;
                } catch (NumberFormatException nfe) {
                    throw new ReadExcelError("Falsche Bewertung in Spalte G in Zeile " + (rowNr + 1));
                }
            }

            Teilnehmer teilnehmer = new Teilnehmer(pruefung);
            teilnehmer.setMatrNr(matrNr);
            teilnehmer.setNachname(nachname);
            teilnehmer.setVorname(vorname);
            teilnehmer.setNote(note);
            teilnehmerRepository.save(teilnehmer);

            ++rowNr;
        }
    }

}
