package com.alexanderfoerster.data;

import com.alexanderfoerster.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "pruefung")
public class Pruefung extends AbstractEntity {
    private LocalDate datum;
    private int anzTeilnehmer;
    private String bezeichnung;

    public Pruefung() {

    }

    public Pruefung(LocalDate datum, int anzTeilnehmer, String bezeichnung) {
        this.datum = datum;
        this.anzTeilnehmer = anzTeilnehmer;
        this.bezeichnung = bezeichnung;
    }

    public LocalDate getDatum() {
        return datum;
    }
    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }
    public int getAnzTeilnehmer() {
        return anzTeilnehmer;
    }
    public void setAnzTeilnehmer(int anzTeilnehmer) {
        this.anzTeilnehmer = anzTeilnehmer;
    }
    public String getBezeichnung() {
        return bezeichnung;
    }
    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }
}