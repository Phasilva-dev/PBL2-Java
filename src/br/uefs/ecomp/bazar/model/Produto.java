package br.uefs.ecomp.bazar.model;

import java.io.*;

public class Produto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;
    private String tipo;

    private String descricaoResumida;

    private String descricaoDetalhada;

    private final Usuario vendedor;

    private boolean vendido;

    private boolean anunciado;


    public String getTipo() {
        return tipo;
    }

    public String getDescricaoResumida() {
        return descricaoResumida;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }



    public Produto(String tipo, String descricaoResumida, String descricaoDetalhada, Usuario vendedor){
        this.tipo = tipo;
        this.descricaoResumida = descricaoResumida;
        this.descricaoDetalhada = descricaoDetalhada;
        this.vendedor = vendedor;
        this.vendido = false;
        this.anunciado = false;
    }
    public boolean isVendido() {
        return vendido;
    }

    public boolean isAnunciado() {
        return anunciado;
    }

    public void setVendido() {
        this.vendido = true;
    }
    public void setAnunciado(boolean TrueOrFalse) {
        this.anunciado = TrueOrFalse;
    }

}
