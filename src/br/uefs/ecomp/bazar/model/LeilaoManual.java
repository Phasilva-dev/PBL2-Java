package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;

import java.io.*;
import java.util.*;

public class LeilaoManual extends Leilao{

    @Serial
    private static final long serialVersionUID = 31L;

    public LeilaoManual(double precoMinimo, double incrementoMinimo, Usuario dono, Produto produto) throws LeilaoNaoCadastrouException {
        super(precoMinimo,incrementoMinimo,dono,produto);
    }

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

    /*Checka se o Leilão foi iniciado e depois se o lance é maior que o precoMinimo+IncrimentoMinimo e modifica as
    variaveis para o proximo lance ser maior ainda.*/
    @Override
    public boolean darLance(Usuario usuario, double valor) throws LanceInvalidoException {
        if (this.status == INICIADO) {
            if (valor < this.precoMinimo + this.incrementoMinimo){
                return false;
            }
            Date now = new Date();
            if (termino != null && termino.before(now)) {
                throw new LanceInvalidoException("Leilao Manual já encerrado, Lance Invalido!!!");
            }
            Lance lance = new Lance(usuario,valor);
            lance.setMomento(now);
            if (this.lances == null) {
                this.lances = new LancesList();
            }
            this.lances.adicionarLance(usuario, lance);
            this.ultimoLance = lance;
            this.precoMinimo = this.ultimoLance.getValor() + this.incrementoMinimo;
            return true;
        }
        throw new LanceInvalidoException("Leilao Manual ainda nao foi iniciado");
    }
    /*checka se o Leilao está no estado INICIADO, para ai então dar um Lance minimo (PrecoMinimo+IncrementoMinimo) */
    @Override
    public void darLanceMinimo(Usuario usuario) throws LanceInvalidoException {
        if (this.status == INICIADO) {
            Date now = new Date();
            if (termino != null && termino.before(now)) {
                throw new LanceInvalidoException("Leilao Manual já encerrado, Lance Invalido!!!");
            }
            Lance lance = new Lance(usuario,this.precoMinimo + this.incrementoMinimo);
            lance.setMomento(now);
            if (this.lances == null) {
                lances = new LancesList();
            }
            this.lances.adicionarLance(usuario, lance);
            this.ultimoLance = lance;
            this.precoMinimo = this.ultimoLance.getValor();
            return;
        }
        throw new LanceInvalidoException("Leilao Manual ainda nao foi iniciado");
    }

}
