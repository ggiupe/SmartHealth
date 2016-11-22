package com.example.giuseppe.smarthealth;

/**
 * Created by giuseppe on 09/11/16.
 */
public class Notifica {
    private String tipologia;
    private String nome;
    private String data;
    private String messaggio;
    private String alert_id;

    public Notifica(String tipologia, String nome, String data, String messaggio, String alert_id) {
        this.tipologia = tipologia;
        this.nome = nome;
        this.data = data;
        this.messaggio = messaggio;
        this.alert_id = alert_id;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getalert_id() {
        return alert_id;
    }

    public void setalert_id(String alert_id) {
        this.alert_id = alert_id;
    }

}
