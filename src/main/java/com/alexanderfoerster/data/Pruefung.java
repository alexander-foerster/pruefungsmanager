package com.alexanderfoerster.data;

import com.alexanderfoerster.Teilnehmer;
import com.alexanderfoerster.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pruefung")
public class Pruefung extends AbstractEntity {
    private LocalDate datum;
    private int anzTeilnehmer;
    private String bezeichnung;

    @OneToMany(mappedBy = "pruefung")
    private List<Teilnehmer> teilnehmers = new ArrayList<>();

    public List<Teilnehmer> getTeilnehmers() {
        return teilnehmers;
    }

    public void setTeilnehmers(List<Teilnehmer> teilnehmers) {
        this.teilnehmers = teilnehmers;
    }

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