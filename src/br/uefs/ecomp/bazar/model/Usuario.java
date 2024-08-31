package br.uefs.ecomp.bazar.model;
import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;
import br.uefs.ecomp.bazar.model.exception.ProdutoNaoCadastrouException;

import java.io.*;
import java.util.*;

public class Usuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 7L;
    private String login;
    private String nome;
    private String senha;
    private String cpf;
    private String endereco;
    private String telefone;
    private List<Produto> produtos;
    private List<Leilao> leiloesCadastrados;
    private transient Leilao leilaoSelecionado;
    private List<Leilao> leiloesIniciados;
    private List<Leilao> leiloesEncerrados;
    public String getLogin() {
        return login;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getSenha() {
        return senha;
    }

    public Leilao getLeilaoSelecionado() {
        return leilaoSelecionado;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public Usuario(String login, String nome, String senha, String cpf, String endereco, String telefone) {
        this.login = login;
        this.nome = nome;
        this.senha = senha;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.produtos = new LinkedList<>();
        this.leiloesCadastrados = new LinkedList<>();
        this.leiloesEncerrados = null;
        this.leilaoSelecionado = null;
    }

    public Iterator<Produto> listarProdutosCadastrados() {
        return this.produtos.iterator();
    }

    public void cadastrarProduto(String tipo, String descricaoResumida, String descricaoDetalhada)
            throws ProdutoNaoCadastrouException {
        if (tipo.isBlank() || descricaoDetalhada.isBlank() || descricaoResumida.isBlank()) {
            throw new ProdutoNaoCadastrouException("Produto nao cadastrou. esta faltando informacao");
        }
        this.produtos.add(new Produto(tipo, descricaoResumida, descricaoDetalhada, this));
    }

    public Leilao cadastrarLeilaoManual(double precoMinimo, double incrementoMinimo, Produto produto)
            throws LeilaoNaoCadastrouException {
        Leilao leilao = new LeilaoManual(precoMinimo, incrementoMinimo, this, produto);
        this.leiloesCadastrados.add(leilao);
        produto.setAnunciado(true);
        return leilao;
    }

    public Leilao cadastrarLeilaoAutomatico(Produto produto, double precoMinimo, double incrementoMinimo, Date inicio, Date termino)
            throws LeilaoNaoCadastrouException {
        Leilao leilao = new LeilaoAutomatico(produto, precoMinimo, incrementoMinimo, this, inicio, termino);
        this.leiloesCadastrados.add(leilao);
        produto.setAnunciado(true);
        return leilao;
    }

    public Leilao cadastrarLeilaoAutomaticoFechado(Produto produto, double precoMinimo, double incrementoMinimo,
                                                   Date inicio, Date termino) throws LeilaoNaoCadastrouException {
        Leilao leilao = new LeilaoAutomaticoFechado(produto, precoMinimo, incrementoMinimo, this, inicio, termino);
        this.leiloesCadastrados.add(leilao);
        produto.setAnunciado(true);
        return leilao;
    }


    public void darLanceMinimo() throws LanceInvalidoException {
        if (this.leilaoSelecionado == null) {
            throw new LanceInvalidoException("Voce nao esta selecionando nenhum leilao");
        }
        if (this.leilaoSelecionado.getVendedor() == this){
            throw new LanceInvalidoException("Voce nao pode dar lance no proprio leilao");
        }
        if (!this.leilaoSelecionado.getParticipantes().contains(this)){
            throw new LanceInvalidoException("Voce nao esta participando desse leilao");
        }
        this.leilaoSelecionado.darLanceMinimo(this);
    }

    public boolean darLance(double valor) throws LanceInvalidoException {
        if (this.leilaoSelecionado == null) {
            throw new LanceInvalidoException("Voce nao esta selecionando nenhum leilao");
        }
        if (this.leilaoSelecionado.getVendedor() == this){
            throw new LanceInvalidoException("Voce nao pode dar lance no proprio leilao");
        }
        if (!this.leilaoSelecionado.getParticipantes().contains(this)){
            throw new LanceInvalidoException("Voce nao esta participando desse leilao");
        }
        return this.leilaoSelecionado.darLance(this, valor);
    }
    public void iniciarLeilao(Leilao leilao) {
        if (this.leiloesCadastrados.contains(leilao)){
            if (this.leiloesIniciados == null){
                this.leiloesIniciados = new LinkedList<>();
            }
            if (leilao instanceof LeilaoManual){
                leilao.setInicio(new Date());
            }
            leilao.iniciar();
            this.leiloesIniciados.add(leilao);
            this.leiloesCadastrados.remove(leilao);
            this.leilaoSelecionado = leilao;
        }
    }
    public void participarLeilao(Leilao leilao) {
        if (leilao.getStatus() != Leilao.ENCERRADO && !leilao.getParticipantes().contains(this)){
            leilao.cadastrarParticipante(this);
            this.leilaoSelecionado = leilao;
        }
    }
    /*Checka se o usuario eh o vendedor desse leilao
    * checka se houve uma venda, caso nao, seta o produto para poder ser anunciado novamente
    * caso haja uma venda, gera a venda e manipula as informacoes*/
    public Venda encerrarLeilaoAtivo() {
        if (this.leilaoSelecionado != null && this.leilaoSelecionado.getVendedor() == this){
            if (this.leiloesEncerrados == null) {
                this.leiloesEncerrados = new LinkedList<>();
            }
            if (this.leilaoSelecionado.getUltimoLance() == null){
                Venda venda = this.leilaoSelecionado.encerrar();
                this.leilaoSelecionado.getProduto().setAnunciado(false);
                this.leiloesEncerrados.add(this.leilaoSelecionado);
                this.leiloesIniciados.remove(this.leilaoSelecionado);
                return venda;
            } else {
                Venda venda = this.leilaoSelecionado.encerrar();
                if (this.leilaoSelecionado instanceof LeilaoManual){
                    Date now = new Date();
                    this.leilaoSelecionado.setTermino(now);
                    this.leilaoSelecionado.getVenda().setHora_da_venda(now);
                }
                this.leiloesEncerrados.add(this.leilaoSelecionado);
                this.leiloesIniciados.remove(this.leilaoSelecionado);
                return venda;
            }
        }
        return null;
    }

    public Leilao selecionarLeilao(Leilao leilao){
        this.leilaoSelecionado = leilao;
        return this.leilaoSelecionado;
    }

}
