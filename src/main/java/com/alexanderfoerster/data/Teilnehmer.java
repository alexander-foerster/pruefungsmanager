package com.alexanderfoerster.data;

import com.alexanderfoerster.data.AbstractEntity;
import com.alexanderfoerster.data.Pruefung;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "teilnehmer")
public class Teilnehmer extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "pruefung_id")
    private Pruefung pruefung;

    private int matrNr;
    private String vorname;
    private String nachname;
    private double note;

    public Teilnehmer() {
        super();
    }

    public Teilnehmer(Pruefung pruefung) {
        this.pruefung = pruefung;
    }

    public Pruefung getPruefung() {
        return pruefung;
    }

    public void setPruefung(Pruefung pruefung) {
        this.pruefung = pruefung;
    }

    public int getMatrNr() {
        return matrNr;
    }

    public void setMatrNr(int matrNr) {
        this.matrNr = matrNr;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }
}