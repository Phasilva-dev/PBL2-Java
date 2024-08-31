package br.uefs.ecomp.bazar.facade;

import java.util.*;

import br.uefs.ecomp.bazar.model.*;
import br.uefs.ecomp.bazar.model.exception.*;

public class BazarFacade {

    ControllerBazar cb;

    public BazarFacade() {
        this.cb = new ControllerBazar();
    }

    //chamadas do cb do usuario
    public Usuario cadastrarUsuario(String login, String nome, String senha,
            String cpf, String endereco, String telefone)
            throws UsuarioNaoCadastrouException {
        return cb.cadastrarUsuario(login, nome, senha, cpf, endereco, telefone);
    }
    
    public Usuario fazerLogin(String login, String senha) throws LoginFalhouException {
        return cb.fazerLogin(login, senha);
    }

    public Produto cadastrarProduto(String tipo, String descricaoResumida,
            String descricaoDetalhada) throws ProdutoNaoCadastrouException {
            return cb.cadastrarProduto(tipo, descricaoResumida, descricaoDetalhada);
    }

    public Iterator<Produto> listarProdutosCadastrados() {
        return this.cb.listarProdutosCadastrados();
    }

    public Leilao cadastrarLeilaoManual(Produto produto, double precoMinimo, double incrementoMinimo)
            throws LeilaoNaoCadastrouException {
        return this.cb.cadastrarLeilaoManual(produto, precoMinimo, incrementoMinimo);
    }
    public Leilao cadastrarLeilaoAutomatico(Produto produto, double precoMinimo, double incrementoMinimo, Date inicio, Date termino)
            throws LeilaoNaoCadastrouException {
        return this.cb.cadastrarLeilaoAutomatico(produto, precoMinimo, incrementoMinimo, inicio, termino);
    }
    public Leilao cadastrarLeilaoAutomaticoFechado(Produto produto, double precoMinimo, double incrementoMinimo, Date inicio, Date termino)
            throws LeilaoNaoCadastrouException {
        return this.cb.cadastrarLeilaoAutomaticoFechado(produto, precoMinimo, incrementoMinimo, inicio, termino);
    }

    public void iniciarLeilao(Leilao leilao) {
        this.cb.iniciarLeilao(leilao);
    }

    public Iterator<Leilao> listarLeiloesIniciados() {
        return this.cb.listarLeiloesIniciados();
    }
    public Iterator<Leilao> listarLeiloesCadastrados() {
        return this.cb.listarLeiloesCadastrados();
    }
    public Iterator<Leilao> listarLeiloesEncerrados() {
        return this.cb.listarLeiloesEncerrados();
    }

    public void participarLeilao(Leilao leilao) {
        this.cb.participarLeilao(leilao);
    }

    public void darLanceMinimo() throws LanceInvalidoException {
            this.cb.darLanceMinimo();
    }

    public boolean darLance(double valor) throws LanceInvalidoException {
            return this.cb.darLance(valor);
    }

    public Venda encerrarLeilao() {
        return this.cb.encerrarLeilao();
    }

    public Iterator<Lance> abrirEnvelopesLeilaoAutomaticoFechado() throws LeilaoNaoEncerradoException{
        return cb.abrirEnvelopesLeilaoAutomaticoFechado();
    }

    public Date listarMomentoAtual(){
        return cb.listarMomentoAtual();
    }

    public Leilao selecionarLeilao(Leilao leilao){
        return cb.selecionarLeilao(leilao);
    }

    public Iterator<Leilao> listarLeiloesTempo(Date comeco, Date fim) throws LeilaoNaoCadastrouException{
        return cb.listarLeiloesTempo(comeco,fim);
    }

    public Iterator<Leilao> listarLeiloes(){
        return cb.listarLeiloes();
    }

    public Iterator<Usuario> listarParticipantesLeilao(){
        return cb.listarParticipantesLeilao();
    }

    public void salvarDados(String diretorio, String arquivo){
        cb.salvarDados(this.cb,diretorio,arquivo);

    }
    public void carregarDados(String diretorio, String arquivo){
        this.cb.carregarDados(diretorio,arquivo);
    }

    public Usuario getLogado(){
        return cb.getLogado();
    }

    public Date getAgora(){
        return cb.getAgora();
    }

    public Iterator<Lance> listarLances() {
        return cb.listarLances();
    }

}
