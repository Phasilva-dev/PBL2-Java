package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;

import java.io.*;
import java.util.*;

public class LeilaoAutomatico extends Leilao{

    @Serial
    private static final long serialVersionUID = 32L;

    public List<Lance> getLances(){
        if (lances == null){
            return null;
        }
        return this.lances.getLancesList();
    }

    public Iterator<Lance> listarLances(){
        if (lances == null){
            return null;
        }
        return this.lances.getLancesList().iterator();
    }

    public LeilaoAutomatico(Produto produto, double precoMinimo, double incrementoMinimo, Usuario dono, Date inicio, Date termino)
            throws LeilaoNaoCadastrouException {
        super(precoMinimo,incrementoMinimo,dono,produto);
        Date now = new Date();
        if (inicio.after(termino)){
            throw new LeilaoNaoCadastrouException("Data invalida, Inicio apos o termino");
        }
        if (now.after(termino)){
            throw new LeilaoNaoCadastrouException("Data invalida, Termino antes da data atual");
        }
        if (inicio.before(now)){
            throw new LeilaoNaoCadastrouException("Data invalida, Inicio antes da data atual");
        }
        this.inicio = inicio;
        this.termino = termino;
    }

    /*Checka se o Leilão foi iniciado e depois se o lance é maior que o precoMinimo+IncrimentoMinimo e modifica as
    variaveis para o proximo lance ser maior ainda.*/
    @Override
    public boolean darLance(Usuario usuario, double valor) throws LanceInvalidoException {
        if (this.status == INICIADO) {
            if (valor < this.precoMinimo + this.incrementoMinimo){
                return false;
            }
            Date now = new Date();
            if (inicio.after(now)) {
                throw new LanceInvalidoException("Leilao Automatico nao esta ativo ainda.");
            }
            if (termino.before(now)) {
                throw new LanceInvalidoException("Leilao Automatico ja nao esta mais ativo.");
            }
            Lance lance = new Lance(usuario,valor);
            lance.setMomento(now);
            if (this.lances == null) {
                lances = new LancesList();
            }
            this.lances.adicionarLance(usuario, lance);
            this.ultimoLance = lance;
            this.precoMinimo = this.ultimoLance.getValor() + this.incrementoMinimo;
            return true;
        }
        throw new LanceInvalidoException("Leilao Automatico ainda nao foi iniciado");
    }
    /*checka se o Leilao está no estado INICIADO, para ai então dar um Lance minimo (PrecoMinimo+IncrementoMinimo) */
    @Override
    public void darLanceMinimo(Usuario usuario) throws LanceInvalidoException {
        if (this.status == INICIADO) {
            Date now = new Date();
            if (inicio.after(now)) {
                throw new LanceInvalidoException("Leilao Automatico nao esta ativo ainda.");
            }
            if (termino.before(now)) {
                throw new LanceInvalidoException("Leilao Automatico ja nao esta mais ativo.");
            }
            Lance lance = new Lance(usuario,this.precoMinimo);
            lance.setMomento(now);
            if (this.lances == null) {
                lances = new LancesList();
            }
            this.lances.adicionarLance(usuario, lance);
            this.ultimoLance = lance;
            this.precoMinimo = this.ultimoLance.getValor() + this.incrementoMinimo;
            return;
        }
        throw new LanceInvalidoException("Leilao Automatico ainda nao foi iniciado");
    }

}
