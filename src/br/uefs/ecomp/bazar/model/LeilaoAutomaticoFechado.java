package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;

import java.io.*;
import java.util.*;

public class LeilaoAutomaticoFechado extends Leilao{

    @Serial
    private static final long serialVersionUID = 33L;

    public LeilaoAutomaticoFechado(Produto produto, double precoMinimo, double incrementoMinimo, Usuario dono, Date inicio, Date termino)
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

    public Map<Usuario, Lance> getLances() {
        if (lances == null){
            return null;
        }
        return lances.getLancesMap();
    }

    @Override
    public Iterator<Lance> listarLances() {
        if (lances == null){
            return null;
        }
        return lances.getLancesMap().values().iterator();
    }

    /*Organiza a lista de lances de forma decrescente e muda o tipo de LanceStrategy para um LinkedHashMap entao retorna*/
    public void organizarLances(){
        List<Map.Entry<Usuario, Lance>> lista = new ArrayList<>(lances.getLancesMap().entrySet());
        lista.sort(new Comparator<Map.Entry<Usuario, Lance>>() {
            @Override
            public int compare(Map.Entry<Usuario, Lance> entry1, Map.Entry<Usuario, Lance> entry2) {
                return Double.compare(entry2.getValue().getValor(), entry1.getValue().getValor());
            }
        });

        Map<Usuario, Lance> mapOrdenado = new LinkedHashMap<>();
        for (Map.Entry<Usuario, Lance> entry : lista) {
            mapOrdenado.put(entry.getKey(), entry.getValue());
        }
        this.lances.setTipoArmazenamento("LinkedHashMap");
        this.lances.setLances(mapOrdenado);
    }

    /*Nao usa o ultimo Lance como verificacao para saber para quem foi vendido, entao se organizar o Map e pega o maior valor*/
    @Override
    public Venda encerrar() {
        if (this.status == INICIADO) {
            if (this.ultimoLance == null){
                this.status = ENCERRADO;
                return this.venda = null;

            } else {
                    this.organizarLances();

                    Map.Entry<Usuario, Lance> maiorLanceEntry = this.lances.getLancesMap().entrySet().iterator().next();

                    this.venda = new Venda(maiorLanceEntry.getValue().getValor(), maiorLanceEntry.getValue().getParticipante(),
                            this.participantes.get(0), this.produto, this);
                this.status = ENCERRADO;
                this.produto.setVendido();
                return this.venda;
            }
        }
        return null;
    }

    /*Checka se o Leilão foi iniciado e se o usuario ja deu um lance
    depois checka se o lance eh maior que o precoMinimo+IncrimentoMinimo.*/
    @Override
    public boolean darLance(Usuario usuario, double valor) throws LanceInvalidoException {
        if (this.status == INICIADO) {
            if (valor < this.precoMinimo + this.incrementoMinimo){
                return false;
            }
            Date now = new Date();
            if (inicio.after(now)) {
                throw new LanceInvalidoException("Leilao Automatico Fechado nao esta ativo ainda.");
            }
            if (termino.before(now)) {
                throw new LanceInvalidoException("Leilao Automatico Fechado ja nao esta mais ativo.");
            }
            if (this.lances != null && this.lances.constainsUsuario(usuario)){
                throw new LanceInvalidoException("Participante ja deu lance nesse leilao");
            }
            Lance lance = new Lance(usuario,valor);
            lance.setMomento(new Date());
            if (this.lances == null) {
                lances = new LancesMap();
            }
            this.ultimoLance = lance;
            this.lances.adicionarLance(usuario,lance);
            return true;
        }
        throw new LanceInvalidoException("Leilao Automatico Fechado ainda nao foi iniciado");
    }

    @Override
    public void darLanceMinimo(Usuario usuario) throws LanceInvalidoException {
        if (this.status == INICIADO) {
            Date now = new Date();
            if (inicio.after(now)) {
                throw new LanceInvalidoException("Leilao Automatico Fechado nao esta ativo ainda.");
            }
            if (termino.before(now)) {
                throw new LanceInvalidoException("Leilao Automatico Fechado ja nao esta mais ativo.");
            }
            if (this.lances != null && this.lances.constainsUsuario(usuario)){
                throw new LanceInvalidoException("Participante ja deu lance nesse leilao");
            }
            Lance lance = new Lance(usuario,this.precoMinimo + this.incrementoMinimo);
            lance.setMomento(new Date());
            if (this.lances == null) {
                lances = new LancesMap();
            }
            this.ultimoLance = lance;
            this.lances.adicionarLance(usuario,lance);
            return;
        }
        throw new LanceInvalidoException("Leilao Automatico Fechado ainda nao foi iniciado");
    }
}