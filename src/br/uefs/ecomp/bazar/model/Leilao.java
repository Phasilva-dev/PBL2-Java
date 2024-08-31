package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;

import java.io.*;
import java.util.*;

public abstract class Leilao implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;
    protected double precoMinimo;
    protected double incrementoMinimo;
    protected Produto produto;
    protected List<Usuario> participantes;
    protected Lance ultimoLance;
    protected LancesStategy lances;
    protected Venda venda;
    protected int status;
    protected Date inicio;
    protected Date termino;
    public static final int CADASTRADO = 0;
    public static final int INICIADO = 1;
    public static final int ENCERRADO = 2;

    public Usuario getVendedor() {
        return participantes.get(0);
    }

    public Produto getProduto() {
        return produto;
    }
    public double getIncrementoMinimo() {
        return incrementoMinimo;
    }
    public double getPrecoMinimo() {
        return precoMinimo;
    }
    public int getStatus(){
        return this.status;
    }

    public abstract Iterator<Lance> listarLances();
    public Lance getUltimoLance() {
        return ultimoLance;
    }
    public Venda getVenda() {
        return venda;
    }
    public List<Usuario> getParticipantes() {
        return participantes;
    }
    public Date getInicio() {
        return inicio;
    }
    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    //Seta o status para o estado INICIADO
    public void iniciar() {
        if (this.status == CADASTRADO) {
            this.status = INICIADO;
        }
    }
    /*Checka se o status é INICIADO, ai então checka se houve pelo menos 1 Lance, caso ocorre efetua uma venda e
    encerra o Leilão caso não ocorra, encerra o leilão e define venda como null */
    public Venda encerrar() {
        if (this.status == INICIADO) {
            if (this.ultimoLance == null){
                this.status = ENCERRADO;
                return this.venda = null;
            } else {
                this.venda = new Venda(this.ultimoLance.getValor(), this.ultimoLance.getParticipante(),
                        this.participantes.get(0), this.produto, this);
                this.status = ENCERRADO;
                this.produto.setVendido();
                return this.venda;
            }
        }
        return null;
    }

    public Leilao(double precoMinimo, double incrementoMinimo, Usuario dono, Produto produto) throws LeilaoNaoCadastrouException {
        if (precoMinimo <= 0 || incrementoMinimo <= 0  || produto == null
                || produto.isAnunciado() || produto.isVendido()){
            throw new LeilaoNaoCadastrouException("Algum dos campos estao vazios ou invalidos");
        }
        this.precoMinimo = precoMinimo;
        this.incrementoMinimo = incrementoMinimo;
        this.produto = produto;
        this.status = CADASTRADO;
        this.participantes = new LinkedList<>();
        this.participantes.add(dono);
        this.inicio = null;
        this.termino = null;
    }

    public void cadastrarParticipante (Usuario participante) {
        if (status != ENCERRADO) {
            this.participantes.add(participante);
        }
    }


    public boolean check_inicio(Date now){
        if (this.termino != null && this.inicio.before(now)){
            return true;
        }
        return false;
    }
    public boolean check_termino(Date now){
        if (this.termino != null && this.termino.before(now)){
            return true;
        }
        return false;
    }


    public abstract boolean darLance(Usuario usuario, double valor) throws LanceInvalidoException;


    public abstract void darLanceMinimo(Usuario usuario) throws LanceInvalidoException;

}