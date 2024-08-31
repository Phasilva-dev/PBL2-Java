package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerBazar implements Serializable {

    @Serial
    private static final long serialVersionUID = 8L;

    private transient Usuario logado;
    Map<String, Usuario> usuarios = new HashMap<>();

    List<Leilao> leiloes = new ArrayList<>();
    private List<Leilao> leiloesCadastrados = new ArrayList<>();
    private List<Leilao> leiloesIniciados = new ArrayList<>();
    private List<Leilao> leiloesEncerrados = new ArrayList<>();

    private transient Date agora = new Date();

    public Date getAgora() {
        return agora;
    }

    public Usuario getLogado() {
        return logado;
    }

    //Cadastra um leilao, adiciona ele a List de leiloes e a list de LeiloesIniciados
    public Leilao cadastrarLeilaoManual(Produto produto, double precoMinimo, double incrementoMinimo)
            throws LeilaoNaoCadastrouException {

        Leilao leilaoCopia = this.logado.cadastrarLeilaoManual(precoMinimo, incrementoMinimo, produto);
        this.leiloes.add(leilaoCopia);
        this.leiloesCadastrados.add(leilaoCopia);
        return leilaoCopia;
    }

    public Leilao cadastrarLeilaoAutomatico(Produto produto, double precoMinimo, double incrementoMinimo, Date inicio, Date termino)
            throws LeilaoNaoCadastrouException {

        Leilao leilaoCopia = this.logado.cadastrarLeilaoAutomatico(produto, precoMinimo, incrementoMinimo, inicio, termino);
        this.leiloes.add(leilaoCopia);
        this.leiloesCadastrados.add(leilaoCopia);
        return leilaoCopia;

    }

    public Leilao cadastrarLeilaoAutomaticoFechado(Produto produto, double precoMinimo, double incrementoMinimo,
                                                   Date inicio, Date termino) throws LeilaoNaoCadastrouException {

        Leilao leilaoCopia = this.logado.cadastrarLeilaoAutomaticoFechado(produto, precoMinimo, incrementoMinimo, inicio, termino);
        this.leiloes.add(leilaoCopia);
        this.leiloesCadastrados.add(leilaoCopia);
        return leilaoCopia;
    }

    /*Atualiza os leiloes e coloca todos em Iterator*/
    public Iterator<Leilao> listarLeiloesIniciados() {
        if (this.logado != null) {
            this.listarMomentoAtual();
            if (this.leiloesIniciados == null) {
                return null;
            }
            return this.leiloesIniciados.iterator();
        }
        return null;
    }
    public Iterator<Leilao> listarLeiloesCadastrados() {
        if (this.logado != null) {
            this.listarMomentoAtual();

            if (this.leiloesCadastrados == null) {
                //Não possui leilões cadastrados
                return null;
            }
            return this.leiloesCadastrados.iterator();
        }
        return null;
    }
    public Iterator<Leilao> listarLeiloesEncerrados() {
        if (this.logado != null) {
            this.listarMomentoAtual();

            if (this.leiloesEncerrados == null) {
                //Não possui leilões cadastrados
                return null;
            }
            return this.leiloesEncerrados.iterator();
        }
        return null;
    }

    //Inicia leiloes, coloca eles na List de iniciados e remove na de cadastrados
    public void iniciarLeilao(Leilao leilao) {
        if (this.logado != null && this.leiloesCadastrados != null &&
                leilao.getStatus() == Leilao.CADASTRADO && this.leiloesCadastrados.contains(leilao)) {
            this.leiloesCadastrados.remove(leilao);
            this.leiloesIniciados.add(leilao);
            this.logado.iniciarLeilao(leilao);
        }
    }

    //Cadastra um novo usuario, nao permite logins repetidos
    public Usuario cadastrarUsuario(String login, String nome, String senha, String cpf, String endereco, String telefone)
            throws UsuarioNaoCadastrouException {
        if (login.isBlank() || nome.isBlank() || senha.isBlank() || cpf.isBlank() || endereco.isBlank() || telefone.isBlank()) {
            throw new UsuarioNaoCadastrouException("Algum dos campos de cadastro esta vazio, Usuario nao cadastrado");
        }
        if (this.usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastrouException("Login ja existente");
        }
        Usuario user = new Usuario(login, nome, senha, cpf, endereco, telefone);
        usuarios.put(login, user);
        return user;
    }


    //Faz login checkando se existe o par <login,senha>, setando o usuario Logado
    public Usuario fazerLogin(String login, String senha) throws LoginFalhouException {
        if (!this.usuarios.containsKey(login)) {
            throw new LoginFalhouException("Usuario nao cadastrado");
        }
        if (this.usuarios.get(login).getSenha().equals(senha)) {
            //Logado com sucesso
            this.logado = this.usuarios.get(login);
            this.listarMomentoAtual();
            return this.logado;
        } else {
            throw new LoginFalhouException("Credenciais erradas");
        }
    }

    public Produto cadastrarProduto(String tipo, String descricaoResumida, String descricaoDetalhada)
            throws ProdutoNaoCadastrouException {
        if (this.logado != null) {
            this.logado.cadastrarProduto(tipo, descricaoResumida, descricaoDetalhada);
            this.listarMomentoAtual();
            return this.logado.getProdutos().get(this.logado.getProdutos().size() - 1);
        } else {
            //Não tem Usuario logado
            return null;
        }
    }

    public Iterator<Produto> listarProdutosCadastrados() {
        if (this.logado != null) {
            return this.logado.listarProdutosCadastrados();
        }
        return null;
    }

    //checka se o leilao esta iniciado ou cadastrado, entao coloca o usuario logado como um participante desse leilao
    public void participarLeilao(Leilao leilao) {
        if (this.logado != null) {
            this.listarMomentoAtual();
            if (this.leiloesIniciados.contains(leilao)) {
                for (int i = 0; i < this.leiloesIniciados.size(); i++) {
                    if (this.leiloesIniciados.get(i) == leilao) {
                        this.logado.participarLeilao(this.leiloesIniciados.get(i));
                        return;
                    }
                }
            } else if (this.leiloesCadastrados.contains(leilao)) {
                for (int i = 0; i < this.leiloesCadastrados.size(); i++) {
                    if (this.leiloesCadastrados.get(i) == leilao) {
                        this.logado.participarLeilao(this.leiloesCadastrados.get(i));
                        return;
                    }
                }
            }
        }
    }

    public void darLanceMinimo() throws LanceInvalidoException {
        if (this.logado != null) {
            this.listarMomentoAtual();
            this.logado.darLanceMinimo();
        } else {
            throw new LanceInvalidoException("Lance invalido, Necessario logar em uma conta");
        }
    }

    public boolean darLance(double valor) throws LanceInvalidoException {
        if (this.logado != null) {
            this.listarMomentoAtual();
            return this.logado.darLance(valor);
        } else {
            throw new LanceInvalidoException("Lance invalido, Necessario logar em uma conta");
        }
    }

    //Encerra leilao selecionado, move ele de iniciado para encerado,
    public Venda encerrarLeilao() {
        if (this.logado != null && this.leiloesIniciados.contains(this.logado.getLeilaoSelecionado())) {
            this.leiloesIniciados.remove(this.logado.getLeilaoSelecionado());
            this.leiloesEncerrados.add(this.logado.getLeilaoSelecionado());
            return this.logado.encerrarLeilaoAtivo();
        }
        return null;
    }

    public Iterator<Lance> listarLances() {
        if (this.logado != null && this.logado.getLeilaoSelecionado() != null
                && this.logado.getLeilaoSelecionado().getVendedor() == this.logado) {
            return this.logado.getLeilaoSelecionado().listarLances();
        }
        return null;
    }

    /*Checka se um leilao automatico ou fechado que esta cadastrado deveria estar iniciado e entao inicia ele
    * depois checka se um leilao qualquer que esta iniciado ja deveria estar encerrado e entao encerra ele*/
    public void checkarLeiloes() {
        if (this.logado != null) {
            for (int i = 0; i < this.leiloesCadastrados.size(); i++) {
                Leilao leilaocopia = this.leiloesCadastrados.get(i);
                if (leilaocopia instanceof LeilaoAutomatico && leilaocopia.check_inicio(this.agora)) {
                    this.leiloesCadastrados.get(i).getVendedor().iniciarLeilao(leilaocopia);
                    this.leiloesIniciados.add(leilaocopia);
                    this.leiloesCadastrados.remove(leilaocopia);
                } else if (leilaocopia instanceof LeilaoAutomaticoFechado && leilaocopia.check_inicio(this.agora)){
                    this.leiloesCadastrados.get(i).getVendedor().iniciarLeilao(leilaocopia);
                    this.leiloesIniciados.add(leilaocopia);
                    this.leiloesCadastrados.remove(leilaocopia);
                }
            }
            for (int i = 0; i < this.leiloesIniciados.size(); i++) {
                Leilao leilaocopia = this.leiloesIniciados.get(i);
                if (leilaocopia.getTermino() != null && leilaocopia.check_termino(this.agora)) {
                    this.leiloesIniciados.get(i).getVendedor().encerrarLeilaoAtivo();
                    this.leiloesEncerrados.add(leilaocopia);
                    this.leiloesIniciados.remove(leilaocopia);
                }
            }
        }
    }

    /*Checka se o leilao eh o fechado e se ele ja terminou, entao revela os lances*/
    public Iterator<Lance> abrirEnvelopesLeilaoAutomaticoFechado() throws LeilaoNaoEncerradoException {
        this.listarMomentoAtual();
        if (this.logado != null && this.logado.getLeilaoSelecionado() != null
                && this.logado.getLeilaoSelecionado() instanceof LeilaoAutomaticoFechado) {
            Leilao leilao = this.logado.getLeilaoSelecionado();
            if (leilao.getStatus() != Leilao.ENCERRADO) {
                throw new LeilaoNaoEncerradoException("Leilao ainda nao foi encerrado.");
            }
            return leilao.listarLances();
        }
        return null;
    }


    public Iterator<Usuario> listarParticipantesLeilao() {
        if (this.logado != null) {
            if (this.logado.getLeilaoSelecionado() != null &&
                    this.logado.getLeilaoSelecionado().getVendedor() == this.logado) {
                return this.logado.getLeilaoSelecionado().getParticipantes().iterator();

            }
        }
        return null;
    }

    //Atualia otempo e todos os leiloes
    public Date listarMomentoAtual() {
        this.agora = new Date();
        this.checkarLeiloes();
        return this.agora;
    }

    /*Comparator para ordenar leiloes pela sua data de inicio, os leiloes manuais no qual o inicio ainda é null
    * são colocados no final da lista*/
    private static final Comparator<Leilao> LEILAO_COMPARATOR = new Comparator<Leilao>() {
        @Override
        public int compare(Leilao o1, Leilao o2) {
            Date inicio1 = o1.getInicio();
            Date inicio2 = o2.getInicio();
            if (inicio1 == null && inicio2 == null) {
                return 0; // Se ambos são nulos, considera-os iguais
            } else if (inicio1 == null) {
                return 1; // Se apenas o início de o1 é nulo, considera o1 maior
            } else if (inicio2 == null) {
                return -1; // Se apenas o início de o2 é nulo, considera o2 maior
            }
            return inicio1.compareTo(inicio2);

        }
    };

    //Organiza os leiloes em ordem crescente pela sua data de inicio e entao retorna um iterator dessa lista
    public Iterator<Leilao> listarLeiloes() {
        if (this.logado != null) {
            this.listarMomentoAtual();
            Collections.sort(this.leiloes, LEILAO_COMPARATOR);
            return this.leiloes.iterator();
        }
        return null;
    }

    /*Organiza os leiloes por tempo e faz uma busca binaria para encontrar os leiloes realizados entre o inicio e fim
    * retorna um iterator desses leiloes*/
    public Iterator<Leilao> listarLeiloesTempo(Date comeco, Date fim) throws LeilaoNaoCadastrouException {
        if (this.logado != null) {
            Collections.sort(this.leiloes, LEILAO_COMPARATOR);

            // Cria um leilão fictício para a busca binária
            Usuario u = new Usuario("1","1","1","1","1","1");
            Produto p = new Produto("1","1","1",u);
            Leilao busca = new LeilaoManual(1, 1, u, p);
            busca.setInicio(comeco);

            int i = Collections.binarySearch(this.leiloes, busca, LEILAO_COMPARATOR);
            if (i < 0) {
                i = -(i + 1);
            }

            List<Leilao> buscaLeilao = new ArrayList<>();
            while (i < this.leiloes.size() && this.leiloes.get(i).getInicio().before(fim)) {
                buscaLeilao.add(this.leiloes.get(i));
                i++;
            }

            return buscaLeilao.iterator();
        }
        return null;
    }

    public Leilao selecionarLeilao(Leilao leilao){
        return this.logado.selecionarLeilao(leilao);
    }

    /*Checka se o diretorio existe, salva tudo que nao esta marcado como transiente e eh serializable*/
    public void salvarDados(ControllerBazar cb,String diretorio, String arquivo) {

        File dir = new File(diretorio);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String caminho = diretorio + File.separator + arquivo;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(cb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Carrega todos os dados que nao sao transient
    public void carregarDados(String diretorio, String arquivo) {
        String caminho = diretorio + File.separator + arquivo;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            ControllerBazar loadedData = (ControllerBazar) ois.readObject();
            this.logado = loadedData.logado;
            this.usuarios = loadedData.usuarios;
            this.leiloes = loadedData.leiloes;
            this.leiloesCadastrados = loadedData.leiloesCadastrados;
            this.leiloesIniciados = loadedData.leiloesIniciados;
            this.leiloesEncerrados = loadedData.leiloesEncerrados;
            this.agora = loadedData.agora;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
