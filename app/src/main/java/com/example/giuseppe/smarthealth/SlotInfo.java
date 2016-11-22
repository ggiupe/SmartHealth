package com.example.giuseppe.smarthealth;

/**
 * Created by giuseppe on 14/11/16.
 */
public class SlotInfo {
    private String titolo;
    private String descrizione;
    private int nome_descr;

    public SlotInfo(String titolo, String descrizione, int nome_descr) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.nome_descr = nome_descr;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getNome_descr() {
        return nome_descr;
    }

    public void setNome_descr(int nome_descr) {
        this.nome_descr = nome_descr;
    }

}
