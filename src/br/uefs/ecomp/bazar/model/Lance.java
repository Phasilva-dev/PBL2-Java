package br.uefs.ecomp.bazar.model;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

public final class Lance implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private double valor;

    private Usuario participante;

    private Date momento;

    public Usuario getParticipante(){
        return this.participante;
    }
    public double getValor(){
        return this.valor;
    }

    public Date getMomento() {
        return momento;
    }

    public Lance(Usuario participante, double valor){
        this.valor = valor;
        this.participante = participante;
    }
    public void setValor(double valor){
        this.valor = valor;
    }
    public void setParticipante(Usuario participante){
        this.participante = participante;
    }

    public void setMomento(Date momento) {
        this.momento = momento;
    }

}
