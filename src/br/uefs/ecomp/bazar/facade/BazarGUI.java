package br.uefs.ecomp.bazar.facade;

import br.uefs.ecomp.bazar.model.*;
import br.uefs.ecomp.bazar.model.exception.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class BazarGUI {


    // Declaracao dos componentes
    private JFrame frame;
    private JPanel containerPrincipal;
    private BazarFacade facade;

    private JPanel painelNorte;
    private JPanel painelOeste;
    private JButton botaoUsuario;
    private JButton botaoTempo;
    private JButton botaoLeilao;

    private JList auxiliar;

    private String stringAuxiliarTitulo;

    private static final int LOGADO = 0;
    private static final int SISTEMA = 1;


    public BazarGUI() {
        iniciar();
    }

    private void iniciar() {


        facade = new BazarFacade();

        frame = new JFrame("Bazar PBL");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Deseja salvar antes de sair?",
                        "Salvar antes de sair",
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    salvarDados();
                    System.exit(0);
                } else if (confirm == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });
        frame.setSize(500, 600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        containerPrincipal = new JPanel(new BorderLayout());
        frame.setContentPane(containerPrincipal);
        configurarPainelNorte();
        configurarPainelOeste();
        containerPrincipal.add(painelNorte, BorderLayout.NORTH);
        containerPrincipal.add(painelOeste, BorderLayout.WEST);

        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }

    private void configurarPainelNorte() {
        painelNorte = new JPanel(new GridLayout(1, 3));
        containerPrincipal.add(painelNorte, BorderLayout.NORTH);

        botaoUsuario = new JButton("Clique aqui para ver o usuario conectado");
        botaoUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    showUsuarioInfoDialog(facade.getLogado());
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });

        painelNorte.add(botaoUsuario);

        botaoLeilao = new JButton("Clique aqui para ver o leilao selecionado");
        botaoLeilao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null && facade.cb.getLogado().getLeilaoSelecionado() != null) {
                    facade.listarMomentoAtual();
                    atualizarTempo();
                    showLeilaoInfoDialog(facade.getLogado().getLeilaoSelecionado(), false, "Norte");
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes ou apenas falta selecionar um leilao");
                }
            }
        });
        painelNorte.add(botaoLeilao);


        botaoTempo = new JButton("Clique aqui para atualizar o tempo");
        botaoTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    facade.listarMomentoAtual();
                    atualizarTempo();
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });
        painelNorte.add(botaoTempo);
    }

    private void configurarPainelOeste() {
        painelOeste = new JPanel(new GridLayout(0, 1));
        containerPrincipal.add(painelOeste, BorderLayout.WEST);

        JButton botaoCadastrarProduto = new JButton("Cadastrar produto");
        botaoCadastrarProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    cadastrarProdutoDialog();
                    atualizarTempo();
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });
        painelOeste.add(botaoCadastrarProduto, BorderLayout.WEST);

        JButton botaoCadastrarLeilao = new JButton("Cadastrar leilao");
        botaoCadastrarLeilao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    showCadastrarLeilaoDialog();
                    atualizarTempo();
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });
        painelOeste.add(botaoCadastrarLeilao);

        JButton botaoListarLeiloes = new JButton("Listar leiloes");
        botaoListarLeiloes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    showEscolherFormaDeListarLeilaoDialog();
                    atualizarTempo();
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });
        painelOeste.add(botaoListarLeiloes);

        JButton botaoListarleiloesTempo = new JButton("Buscar leiloes por tempo");
        botaoListarleiloesTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    showLeiloesTempoDialog();
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });
        painelOeste.add(botaoListarleiloesTempo);

        JButton botaoListarProdutos = new JButton("Listar meus produtos");
        botaoListarProdutos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facade.cb.getLogado() != null) {
                    showListarProdutoDialog();
                } else {
                    JOptionPane.showMessageDialog(frame, "Faca login antes");
                }
            }
        });
        painelOeste.add(botaoListarProdutos);

    }

    private void atualizarTempo() {
        Date date = facade.cb.getAgora();
        SimpleDateFormat formatado = new SimpleDateFormat("HH:mm:ss");
        String DataFormatado = formatado.format(date);
        botaoTempo.setText("Ultima atualizacao de tempo: " + DataFormatado);
    }

    private void cadastrarProdutoDialog() {
        JDialog cadastroProdutoDialog = new JDialog(frame, "Cadastrar Produto", true);
        cadastroProdutoDialog.setSize(400, 300);
        cadastroProdutoDialog.setLayout(new GridLayout(4, 2));

        JLabel labelTipo = new JLabel("Tipo:");
        JTextField textTipo = new JTextField();
        JLabel labelDescricaoResumida = new JLabel("Descricao resumida:");
        JTextField textDescricaoResumida = new JTextField();
        JLabel labelDescricaoDetalhada = new JLabel("DescricaoDetalhada:");
        JTextField textDescricaoDetalhada = new JTextField();

        JButton buttonOk = new JButton("Cadastrar");
        JButton buttonFechar = new JButton("Fechar");
        buttonOk.setBackground(new Color(34, 139, 34));
        buttonFechar.setBackground(new Color(189, 34, 34));

        cadastroProdutoDialog.add(labelTipo);
        cadastroProdutoDialog.add(textTipo);
        cadastroProdutoDialog.add(labelDescricaoResumida);
        cadastroProdutoDialog.add(textDescricaoResumida);
        cadastroProdutoDialog.add(labelDescricaoDetalhada);
        cadastroProdutoDialog.add(textDescricaoDetalhada);
        cadastroProdutoDialog.add(buttonOk);
        cadastroProdutoDialog.add(buttonFechar);

        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipo = textTipo.getText();
                String DescricaoResumida = textDescricaoResumida.getText();
                String DescricaoDetalhada = textDescricaoDetalhada.getText();
                try {
                    facade.cadastrarProduto(tipo, DescricaoResumida, DescricaoDetalhada);
                    JOptionPane.showMessageDialog(frame, "Cadastro realizado com sucesso!");
                    cadastroProdutoDialog.dispose();
                } catch (ProdutoNaoCadastrouException ex) {
                    JOptionPane.showMessageDialog(frame, "Cadastro do produto falhou: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastroProdutoDialog.dispose();
            }
        });

        cadastroProdutoDialog.setLocationRelativeTo(frame);
        cadastroProdutoDialog.setVisible(true);
    }

    private void showListarProdutoDialog() {
        JDialog listarProdutoDialog = new JDialog(frame, "Produtos Cadastrados", true);
        listarProdutoDialog.setSize(400, 300);
        listarProdutoDialog.setLayout(new GridLayout(1, 2));

        JLabel label = new JLabel("Produtos:");
        JList listProdutos = IteratorProdutoToJList();


        listarProdutoDialog.add(label);
        listarProdutoDialog.add(new JScrollPane(listProdutos));

        listarProdutoDialog.setLocationRelativeTo(frame);
        listarProdutoDialog.setVisible(true);

    }

    static class ProdutoListarCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Produto) {
                Produto produto = (Produto) value;
                setText(produto.getDescricaoDetalhada());
            }
            return this;
        }
    }

    static class ProdutoComboBoxRenderer extends JLabel implements ListCellRenderer<Produto> {
        public ProdutoComboBoxRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Produto> list, Produto value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if (value != null) {
                setText(value.getDescricaoDetalhada());
            }

            return this;
        }
    }

    static class LanceListarCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Lance) {
                Lance lance = (Lance) value;
                setText("Participante: " + lance.getParticipante().getNome() + " Valor do Lance: " + lance.getValor());
            }
            return this;
        }
    }

    static class LeilaoListarCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Leilao) {
                Leilao leilao = (Leilao) value;
                setText("Leilao de " + leilao.getProduto().getDescricaoDetalhada() + " vendido por " + leilao.getVendedor().getLogin());
            }
            return this;
        }
    }

    static class UsuarioListarCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Usuario) {
                Usuario usuario = (Usuario) value;
                setText(usuario.getLogin());
            }
            return this;
        }
    }

    private JList<Produto> IteratorProdutoToJList() {

        Iterator<Produto> iterator = facade.listarProdutosCadastrados();
        LinkedList<Produto> lista = new LinkedList<>();

        if (iterator == null) {
            return new JList<>();
        }

        while (iterator.hasNext()) {
            lista.add(iterator.next());
        }

        Produto[] array = lista.toArray(new Produto[0]);
        JList<Produto> list = new JList<>(array);
        list.setCellRenderer(new ProdutoListarCellRenderer());
        return list;

    }

    private void showCadastrarLeilaoDialog() {
        JDialog cadastroLeilaoDialog = new JDialog(frame, "Deseja cadastrar qual tipo de leilao?", true);
        cadastroLeilaoDialog.setSize(400, 300);
        cadastroLeilaoDialog.setLayout(new GridLayout(3, 1));

        JButton buttonManual = new JButton("Leilao Manual");
        JButton buttonAutomatico = new JButton("Leilao Automatico");
        JButton buttonFechado = new JButton("Leilao Automatico Fechado");


        buttonManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarTempo();
                cadastroLeilaoDialog.dispose();
                showCadastroManualDialog();

            }
        });
        buttonAutomatico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarTempo();
                cadastroLeilaoDialog.dispose();
                showCadastroAutomaticoDialog();
            }
        });
        buttonFechado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarTempo();
                cadastroLeilaoDialog.dispose();
                showCadastroFechadoDialog();
            }
        });


        cadastroLeilaoDialog.add(buttonManual);
        cadastroLeilaoDialog.add(buttonAutomatico);
        cadastroLeilaoDialog.add(buttonFechado);


        cadastroLeilaoDialog.setLocationRelativeTo(frame);
        cadastroLeilaoDialog.setVisible(true);

    }

    private JComboBox<Produto> IteratorProdutoToJComboBox() {

        Iterator<Produto> iterator = facade.listarProdutosCadastrados();
        LinkedList<Produto> lista = new LinkedList<>();

        if (iterator == null) {
            return new JComboBox<>();
        }

        while (iterator.hasNext()) {
            Produto p = iterator.next();
            if (!p.isAnunciado()) {
                lista.add(p);
            }
        }

        Produto[] array = lista.toArray(new Produto[0]);
        JComboBox<Produto> comboBox = new JComboBox<>(array);
        comboBox.setRenderer(new ProdutoComboBoxRenderer());
        return comboBox;
        //return new JList(array);
    }

    private void showCadastroManualDialog() {
        JDialog cadastroManualDialog = new JDialog(frame, "Cadastro leilao manual", true);
        cadastroManualDialog.setSize(400, 300);
        cadastroManualDialog.setLayout(new GridLayout(4, 2));

        JLabel labelProduto = new JLabel("Produtos:");
        JComboBox comboBoxProduto = IteratorProdutoToJComboBox();
        JLabel labelValor = new JLabel("Preco inicial:");
        JTextField textValor = new JTextField();
        JLabel labelIncremento = new JLabel("Incremento minimo:");
        JTextField textIncremento = new JTextField();
        JButton buttonCadastrar = new JButton("Cadastrar");
        JButton buttonFechar = new JButton("Fechar");

        buttonCadastrar.setBackground(new Color(34, 139, 34));
        buttonFechar.setBackground(new Color(189, 34, 34));

        buttonCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Produto produto = (Produto) comboBoxProduto.getSelectedItem();
                String valorText = textValor.getText();
                String incrementoText = textIncremento.getText();
                try {
                    double valor = Double.parseDouble(valorText);
                    double incremento = Double.parseDouble(incrementoText);
                    facade.cadastrarLeilaoManual(produto, valor, incremento);
                    atualizarTempo();
                    JOptionPane.showMessageDialog(frame, "Leilao cadastrado com sucesso!");
                    cadastroManualDialog.dispose();
                } catch (NumberFormatException | LeilaoNaoCadastrouException ex) {
                    JOptionPane.showMessageDialog(frame, "Erro ao cadastrar leilao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastroManualDialog.dispose();
                showCadastrarLeilaoDialog();
            }
        });

        cadastroManualDialog.add(labelProduto);
        cadastroManualDialog.add(comboBoxProduto);
        cadastroManualDialog.add(labelValor);
        cadastroManualDialog.add(textValor);
        cadastroManualDialog.add(labelIncremento);
        cadastroManualDialog.add(textIncremento);
        cadastroManualDialog.add(buttonCadastrar);
        cadastroManualDialog.add(buttonFechar);

        cadastroManualDialog.setLocationRelativeTo(frame); // Centralizar o diálogo na janela principal
        cadastroManualDialog.setVisible(true); // Mostrar o diálogo
    }

    private void showCadastroAutomaticoDialog() {

        JDialog cadastroAutomaticoDialog = new JDialog(frame, "Cadastro leilao automatico", true);
        cadastroAutomaticoDialog.setSize(400, 300);
        cadastroAutomaticoDialog.setLayout(new GridLayout(6, 2));

        JLabel labelProduto = new JLabel("Produtos:");
        JComboBox comboBoxProduto = IteratorProdutoToJComboBox();
        JLabel labelValor = new JLabel("Preco inicial:");
        JTextField textValor = new JTextField();
        JLabel labelIncremento = new JLabel("Incremento minimo:");
        JTextField textIncremento = new JTextField();
        JLabel labelInicio = new JLabel("Inicio:");
        JSpinner spinnerInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorInicio = new JSpinner.DateEditor(spinnerInicio, "dd/MM/yyyy HH:mm");
        spinnerInicio.setEditor(dateEditorInicio);
        JLabel labelTermino = new JLabel("Termino:");
        JSpinner spinnerTermino = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorTermino = new JSpinner.DateEditor(spinnerTermino, "dd/MM/yyyy HH:mm");
        spinnerTermino.setEditor(dateEditorTermino);
        JButton buttonCadastrar = new JButton("Cadastrar");
        JButton buttonFechar = new JButton("Fechar");

        buttonCadastrar.setBackground(new Color(34, 139, 34));
        buttonFechar.setBackground(new Color(189, 34, 34));

        buttonCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Produto produto = (Produto) comboBoxProduto.getSelectedItem();
                String valorText = textValor.getText();
                String incrementoText = textIncremento.getText();
                Date inicio = (Date) spinnerInicio.getValue();
                Date termino = (Date) spinnerTermino.getValue();
                try {
                    double valor = Double.parseDouble(valorText);
                    double incremento = Double.parseDouble(incrementoText);
                    facade.cadastrarLeilaoAutomatico(produto, valor, incremento, inicio, termino);
                    atualizarTempo();
                    JOptionPane.showMessageDialog(frame, "Leilao cadastrado com sucesso!");
                    cadastroAutomaticoDialog.dispose();
                } catch (NumberFormatException | LeilaoNaoCadastrouException ex) {
                    JOptionPane.showMessageDialog(frame, "Erro ao cadastrar leilao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastroAutomaticoDialog.dispose();
                showCadastrarLeilaoDialog();
            }
        });

        cadastroAutomaticoDialog.add(labelProduto);
        cadastroAutomaticoDialog.add(comboBoxProduto);
        cadastroAutomaticoDialog.add(labelValor);
        cadastroAutomaticoDialog.add(textValor);
        cadastroAutomaticoDialog.add(labelIncremento);
        cadastroAutomaticoDialog.add(textIncremento);
        cadastroAutomaticoDialog.add(labelInicio);
        cadastroAutomaticoDialog.add(spinnerInicio);
        cadastroAutomaticoDialog.add(labelTermino);
        cadastroAutomaticoDialog.add(spinnerTermino);
        cadastroAutomaticoDialog.add(buttonCadastrar);
        cadastroAutomaticoDialog.add(buttonFechar);

        cadastroAutomaticoDialog.setLocationRelativeTo(frame);
        cadastroAutomaticoDialog.setVisible(true);
    }

    private void showCadastroFechadoDialog() {

        JDialog cadastroFechadoDialog = new JDialog(frame, "Cadastro leilao automatico", true);
        cadastroFechadoDialog.setSize(400, 300);
        cadastroFechadoDialog.setLayout(new GridLayout(6, 2));

        JLabel labelProduto = new JLabel("Produtos:");
        JComboBox comboBoxProduto = IteratorProdutoToJComboBox();
        JLabel labelValor = new JLabel("Preco inicial:");
        JTextField textValor = new JTextField();
        JLabel labelIncremento = new JLabel("Incremento minimo:");
        JTextField textIncremento = new JTextField();
        JLabel labelInicio = new JLabel("Inicio:");
        JSpinner spinnerInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorInicio = new JSpinner.DateEditor(spinnerInicio, "dd/MM/yyyy HH:mm");
        spinnerInicio.setEditor(dateEditorInicio);
        JLabel labelTermino = new JLabel("Termino:");
        JSpinner spinnerTermino = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorTermino = new JSpinner.DateEditor(spinnerTermino, "dd/MM/yyyy HH:mm");
        spinnerTermino.setEditor(dateEditorTermino);
        JButton buttonCadastrar = new JButton("Cadastrar");
        JButton buttonFechar = new JButton("Fechar");

        buttonCadastrar.setBackground(new Color(34, 139, 34));
        buttonFechar.setBackground(new Color(189, 34, 34));

        buttonCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Produto produto = (Produto) comboBoxProduto.getSelectedItem();
                String valorText = textValor.getText();
                String incrementoText = textIncremento.getText();
                Date inicio = (Date) spinnerInicio.getValue();
                Date termino = (Date) spinnerTermino.getValue();
                try {
                    double valor = Double.parseDouble(valorText);
                    double incremento = Double.parseDouble(incrementoText);
                    facade.cadastrarLeilaoAutomaticoFechado(produto, valor, incremento, inicio, termino);
                    atualizarTempo();
                    JOptionPane.showMessageDialog(frame, "Leilao cadastrado com sucesso!");
                    cadastroFechadoDialog.dispose();
                } catch (NumberFormatException | LeilaoNaoCadastrouException ex) {
                    JOptionPane.showMessageDialog(frame, "Erro ao cadastrar leilao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastroFechadoDialog.dispose();
                showCadastrarLeilaoDialog();
            }
        });

        cadastroFechadoDialog.add(labelProduto);
        cadastroFechadoDialog.add(comboBoxProduto);
        cadastroFechadoDialog.add(labelValor);
        cadastroFechadoDialog.add(textValor);
        cadastroFechadoDialog.add(labelIncremento);
        cadastroFechadoDialog.add(textIncremento);
        cadastroFechadoDialog.add(labelInicio);
        cadastroFechadoDialog.add(spinnerInicio);
        cadastroFechadoDialog.add(labelTermino);
        cadastroFechadoDialog.add(spinnerTermino);
        cadastroFechadoDialog.add(buttonCadastrar);
        cadastroFechadoDialog.add(buttonFechar);

        cadastroFechadoDialog.setLocationRelativeTo(frame);
        cadastroFechadoDialog.setVisible(true);
    }

    private void showEscolherFormaDeListarLeilaoDialog() {
        JDialog listarLeilaoDialog = new JDialog(frame, "Escolha como quer listar os leiloes", true);
        listarLeilaoDialog.setSize(800, 450);
        listarLeilaoDialog.setLayout(new GridLayout(2, 5));

        JLabel labelTodos = new JLabel("Listar leiloes do sistema:");
        JLabel labelMeus = new JLabel("listar leiloes do usuario");
        JButton buttonLeilao = new JButton("Todos Leiloes");
        JButton buttonLeilaoCadastrado = new JButton("Cadastrados");
        JButton buttonLeilaoIniciado = new JButton("Iniciados");
        JButton buttonLeilaoEncerrado = new JButton("Encerrados");
        JButton buttonMeuLeilao = new JButton("Meus leiloes");
        JButton buttonMeuLeilaoCadastrado = new JButton("Meus cadastrados");
        JButton buttonMeuLeilaoIniciado = new JButton("Meus iniciados");
        JButton buttonMeuLeilaoEncerrado = new JButton("Meus encerrados");


        buttonLeilao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoToJList(SISTEMA);
                stringAuxiliarTitulo = "Todos os leiloes";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);

            }
        });
        buttonLeilaoCadastrado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoCadastradoToJList(SISTEMA);
                stringAuxiliarTitulo = "Todos os leiloes cadastrados";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
            }
        });
        buttonLeilaoIniciado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoIniciadoToJList(SISTEMA);
                stringAuxiliarTitulo = "Todos os leiloes iniciados";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
            }
        });
        buttonLeilaoEncerrado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoEncerradoToJList(SISTEMA);
                stringAuxiliarTitulo = "Todos os leiloes encerrados";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
            }
        });
        buttonMeuLeilao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoToJList(LOGADO);
                stringAuxiliarTitulo = "Todos os meus leiloes";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);

            }
        });
        buttonMeuLeilaoCadastrado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoCadastradoToJList(LOGADO);
                stringAuxiliarTitulo = "Todos os meus leiloes cadastrados";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
            }
        });
        buttonMeuLeilaoIniciado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoIniciadoToJList(LOGADO);
                stringAuxiliarTitulo = "Todos os meus leiloes iniciados";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
            }
        });
        buttonMeuLeilaoEncerrado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLeilaoDialog.dispose();
                auxiliar = iteratorLeilaoEncerradoToJList(LOGADO);
                stringAuxiliarTitulo = "Todos os meus leiloes encerrados";
                showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
            }
        });
        listarLeilaoDialog.add(labelTodos);
        listarLeilaoDialog.add(buttonLeilao);
        listarLeilaoDialog.add(buttonLeilaoCadastrado);
        listarLeilaoDialog.add(buttonLeilaoIniciado);
        listarLeilaoDialog.add(buttonLeilaoEncerrado);
        listarLeilaoDialog.add(labelMeus);
        listarLeilaoDialog.add(buttonMeuLeilao);
        listarLeilaoDialog.add(buttonMeuLeilaoCadastrado);
        listarLeilaoDialog.add(buttonMeuLeilaoIniciado);
        listarLeilaoDialog.add(buttonMeuLeilaoEncerrado);


        listarLeilaoDialog.setLocationRelativeTo(frame);
        listarLeilaoDialog.setVisible(true);

    }

    private void showLeilaoListarDialog(JList<Leilao> lista, String title) {
        JDialog leilaoListarDialog = new JDialog(frame, title, true);
        leilaoListarDialog.setSize(400, 300);
        leilaoListarDialog.setLayout(new GridBagLayout());

        JButton buttonSelecionar = new JButton("Selecionar leilao");
        JButton buttonLeilaoInfo = new JButton("Informacoes do leilao");
        JButton buttonFechar = new JButton("Voltar");


        buttonSelecionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lista.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(frame, "Nenhum leilao selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    Leilao leilao = facade.selecionarLeilao(lista.getSelectedValue());
                    botaoLeilao.setText("Leilao Selecionado: Leilao de " + leilao.getProduto().getDescricaoDetalhada() + " vendido por " + leilao.getVendedor().getLogin());
                    JOptionPane.showMessageDialog(frame, "Leilao selecionado com sucesso!");
                    leilaoListarDialog.dispose();
                }
            }
        });

        buttonLeilaoInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lista.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(frame, "Nenhum leilao selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    leilaoListarDialog.dispose();
                    showLeilaoInfoDialog(lista.getSelectedValue(), true, "Oeste");
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leilaoListarDialog.dispose();
                showEscolherFormaDeListarLeilaoDialog();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;
        leilaoListarDialog.add(new JScrollPane(lista), c);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.33;
        c.weighty = 0.0;
        leilaoListarDialog.add(buttonSelecionar, c);

        c.gridx = 1;
        leilaoListarDialog.add(buttonLeilaoInfo, c);

        c.gridx = 2;
        leilaoListarDialog.add(buttonFechar, c);

        leilaoListarDialog.setLocationRelativeTo(frame);
        leilaoListarDialog.setVisible(true);

    }

    private void showLeilaoInfoDialog(Leilao leilao, Boolean modal, String posicao) {
        JDialog leilaoInfoDialog = new JDialog(frame, "Visao geral do leilao", modal);
        leilaoInfoDialog.setSize(400, 300);
        leilaoInfoDialog.setLayout(new GridLayout(6, 2));

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JLabel labelPrecoMinimo = new JLabel("Preco minimo: " + String.valueOf(leilao.getPrecoMinimo()));
        JLabel labelIncrementoMinimo = new JLabel("Incremento minimo: " + String.valueOf(leilao.getIncrementoMinimo()));
        JLabel labelProduto = new JLabel("Produto: " + leilao.getProduto().getDescricaoDetalhada());

        int statusInt = leilao.getStatus();
        String statusString;
        if (statusInt == Leilao.CADASTRADO) {
            statusString = "Cadastrado";
        } else if (statusInt == Leilao.INICIADO) {
            statusString = "Iniciado";
        } else if (statusInt == Leilao.ENCERRADO){
            statusString = "Encerrado";
        } else {
            statusString = "Indefinido";
        }
        JLabel labelStatus = new JLabel("Status: " + statusString);
        JLabel labelInicio;
        JLabel labelTermino;
        if (leilao.getInicio() != null) {
            labelInicio = new JLabel("Inicio: " + formatter.format(leilao.getInicio()));
        } else {
            labelInicio = new JLabel("Inicio: N/A");
        }
        if (leilao.getTermino() != null) {
            labelTermino = new JLabel("Termino: " + formatter.format(leilao.getTermino()));
        } else {
            labelTermino = new JLabel("Termino: N/A");
        }

        if (Objects.equals(posicao, "Oeste")) {
            JButton botaoSelecionar = new JButton("Selecionar leilao");
            botaoSelecionar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    facade.selecionarLeilao(leilao);
                    botaoLeilao.setText("Leilao Selecionado: Leilao de " + leilao.getProduto().getDescricaoDetalhada() + " vendido por " + leilao.getVendedor().getLogin());
                    JOptionPane.showMessageDialog(frame, "Leilao selecionado com sucesso!");
                    leilaoInfoDialog.dispose();
                }
            });

            JButton botaoVoltar = new JButton("Voltar");
            botaoVoltar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    leilaoInfoDialog.dispose();
                    showLeilaoListarDialog(auxiliar, stringAuxiliarTitulo);
                }
            });
            JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
            JButton[] buttons = {botaoSelecionar, null, null, null, null, botaoVoltar};
            configurarJDialog(leilaoInfoDialog, labels, buttons);

        } else if (Objects.equals(posicao, "Norte")) {

            if (leilao.getVendedor() == facade.getLogado()) { //Se ele eh o vendedort

                JButton botaoParticipantes = new JButton("Listar Participantes");
                botaoParticipantes.addActionListener(new ShowParticipantesAction());

                JButton botaoFechar = new JButton("Fechar");
                botaoFechar.addActionListener(new FecharDialogAction(leilaoInfoDialog));

                JButton botaoLances = new JButton("Listar Lances");
                botaoLances.addActionListener(new ShowLancesAction(false));

                if (leilao instanceof LeilaoManual) {

                    if (statusInt == Leilao.CADASTRADO) {
                        JButton botaoIniciar = new JButton("Iniciar leilao");
                        botaoIniciar.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                facade.iniciarLeilao(leilao);
                                JOptionPane.showMessageDialog(frame, "Leilao iniciado com sucesso!");
                                atualizarTempo();
                                leilaoInfoDialog.dispose();
                            }
                        });
                        JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                        JButton[] buttons = {botaoParticipantes, botaoLances, botaoIniciar, null, null, botaoFechar};
                        configurarJDialog(leilaoInfoDialog, labels, buttons);

                    } else if (statusInt == Leilao.INICIADO) {
                        JButton botaoEncerrar = new JButton("Encerrar leilao");
                        botaoEncerrar.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                facade.encerrarLeilao();
                                atualizarTempo();
                                JOptionPane.showMessageDialog(frame, "Leilao encerrado com sucesso!");
                                leilaoInfoDialog.dispose();
                            }
                        });
                        JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                        JButton[] buttons = {botaoParticipantes, botaoLances, botaoEncerrar, null, null, botaoFechar};
                        configurarJDialog(leilaoInfoDialog, labels, buttons);

                    } else {

                        JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                        JButton[] buttons = {botaoParticipantes, botaoLances, null, null, null, botaoFechar};
                        configurarJDialog(leilaoInfoDialog, labels, buttons);

                    }
                } else if (leilao instanceof LeilaoAutomatico) {

                    JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                    JButton[] buttons = {botaoParticipantes, botaoLances, null, null, null, botaoFechar};
                    configurarJDialog(leilaoInfoDialog, labels, buttons);

                } else { //leilao instanceof LeilaoAutomaticoFechado
                    if (statusInt != Leilao.ENCERRADO) {

                        JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                        JButton[] buttons = {botaoParticipantes, null, null, null, null, botaoFechar};
                        configurarJDialog(leilaoInfoDialog, labels, buttons);

                    } else { //statusInt == Leilao.ENCERRADO
                        if (statusInt == Leilao.ENCERRADO) {
                            botaoLances = new JButton("Listar Lances");
                            botaoLances.addActionListener(new ShowLancesAction(true));

                            JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                            JButton[] buttons = {botaoParticipantes, botaoLances, null, null, null, botaoFechar};
                            configurarJDialog(leilaoInfoDialog, labels, buttons);
                        }
                    }
                }
            } else { //Até aqui esta certo e funcionando como deveria
                if (!leilao.getParticipantes().contains(facade.getLogado())) {//Se ele nao eh o vendedor e nem participante
                    if (leilao.getStatus() != Leilao.ENCERRADO) {
                        JButton botaoParticipar = new JButton("Participar desse leilao");
                        botaoParticipar.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                facade.participarLeilao(leilao);
                                atualizarTempo();
                                JOptionPane.showMessageDialog(frame, "Participando do leilao com Sucesso!");
                                leilaoInfoDialog.dispose();
                            }
                        });
                        JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                        JButton[] buttons = {botaoParticipar, null, null, null, null, null};
                        configurarJDialog(leilaoInfoDialog, labels, buttons);

                    } else {
                        JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                        JButton[] buttons = {null, null, null, null, null, null};
                        configurarJDialog(leilaoInfoDialog, labels, buttons);
                    }
                } else { //Usuario nao eh o vendedor porem esta na lista de participantes
                    if (leilao instanceof LeilaoAutomatico || leilao instanceof LeilaoManual) {
                        if (statusInt == Leilao.INICIADO){
                            JButton botaoDarlanceMinimo = new JButton("Dar lance minimo");
                            botaoDarlanceMinimo.addActionListener(new LanceMinimoActionListener(frame,leilaoInfoDialog));
                            JButton botaoDarLance = new JButton("Dar lance");
                            botaoDarLance.addActionListener(new DarLanceActionListener(frame, leilaoInfoDialog));
                            JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                            JButton[] buttons = {botaoDarLance, botaoDarlanceMinimo, null, null, null, null};
                            configurarJDialog(leilaoInfoDialog, labels, buttons);
                        } else {
                            JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                            JButton[] buttons = {null, null, null, null, null, null};
                            configurarJDialog(leilaoInfoDialog, labels, buttons);
                        }

                    } else { //leilao instanceof LeilaoAutomaticoFechado
                        LeilaoAutomaticoFechado leilao1 = (LeilaoAutomaticoFechado) leilao;
                        if (statusInt == Leilao.INICIADO && (leilao1.getLances() == null || !leilao1.getLances().containsKey(facade.getLogado()))) {
                            JButton botaoDarlanceMinimo = new JButton("Dar lance minimo");
                            botaoDarlanceMinimo.addActionListener(new LanceMinimoActionListener(frame,leilaoInfoDialog));
                            JButton botaoDarLance = new JButton("Dar lance");
                            botaoDarLance.addActionListener(new DarLanceActionListener(frame, leilaoInfoDialog));
                            JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                            JButton[] buttons = {botaoDarLance, botaoDarlanceMinimo, null, null, null, null};
                            configurarJDialog(leilaoInfoDialog, labels, buttons);
                        } else {
                            JLabel[] labels = {labelPrecoMinimo, labelIncrementoMinimo, labelProduto, labelStatus, labelInicio, labelTermino};
                            JButton[] buttons = {null, null, null, null, null, null};
                            configurarJDialog(leilaoInfoDialog, labels, buttons);
                        }
                    }
                }
            }
        }

        leilaoInfoDialog.setLocationRelativeTo(frame);
        leilaoInfoDialog.setVisible(true);

    }

    private void showJListJDialog(JList<?> jList, String title) {
        JDialog jListJDialog = new JDialog(frame, title, false);
        jListJDialog.setSize(400, 300);
        jListJDialog.setLayout(new GridLayout(1, 1));
        JScrollPane scrollPane = new JScrollPane(jList);
        jListJDialog.add(scrollPane);
        jListJDialog.setLocationRelativeTo(frame);
        jListJDialog.setVisible(true);


    }

    private JList<Usuario> iteratorUsuarioToJList() {
        Iterator<Usuario> iterator = facade.listarParticipantesLeilao();
        LinkedList<Usuario> lista = new LinkedList<>();

        if (iterator == null) {
            return new JList<>();
        }

        iterator.next(); //O Primeiro sempre é o vendedor

        while (iterator.hasNext()) {
            lista.add(iterator.next());
        }

        Usuario[] array = lista.toArray(new Usuario[0]);
        JList<Usuario> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new UsuarioListarCellRenderer());

        return jlist;
    }

    private JList<Lance> iteratorLanceToJList(boolean abrirEnvelopes) throws LeilaoNaoEncerradoException {
        Iterator<Lance> iterator;

        if (abrirEnvelopes) {
            iterator = facade.abrirEnvelopesLeilaoAutomaticoFechado();
        } else {
            iterator = facade.listarLances();
        }

        LinkedList<Lance> lista = new LinkedList<>();

        if (iterator == null) {
            return new JList<>();
        }

        while (iterator.hasNext()) {
            lista.add(iterator.next());
        }

        Lance[] array = lista.toArray(new Lance[0]);
        JList<Lance> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new LanceListarCellRenderer());

        return jlist;
    }

    private JList<Leilao> iteratorLeilaoToJList(int LOGADO_OU_SISTEMA) {

        Iterator<Leilao> iterator = facade.listarLeiloes();
        atualizarTempo();
        LinkedList<Leilao> lista = new LinkedList<>();
        Usuario user = facade.getLogado();

        if (iterator == null) {
            return new JList<>();
        }
        if (LOGADO_OU_SISTEMA == LOGADO) {

            while (iterator.hasNext()) {
                Leilao leilao = iterator.next();
                if (leilao.getVendedor() == user) {
                    lista.add(leilao);
                }
            }
        }
        if (LOGADO_OU_SISTEMA == SISTEMA) {
            while (iterator.hasNext()) {
                lista.add(iterator.next());
            }
        }
        Leilao[] array = lista.toArray(new Leilao[0]);
        JList<Leilao> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new LeilaoListarCellRenderer());

        return jlist;
    }

    private JList<Leilao> iteratorLeilaoIniciadoToJList(int LOGADO_OU_SISTEMA) {

        Iterator<Leilao> iterator = facade.listarLeiloesIniciados();
        atualizarTempo();
        LinkedList<Leilao> lista = new LinkedList<>();
        Usuario user = facade.getLogado();

        if (iterator == null) {
            return new JList<>();
        }
        if (LOGADO_OU_SISTEMA == LOGADO) {

            while (iterator.hasNext()) {
                Leilao leilao = iterator.next();
                if (leilao.getVendedor() == user) {
                    lista.add(leilao);
                }
            }
        }
        if (LOGADO_OU_SISTEMA == SISTEMA) {
            while (iterator.hasNext()) {
                lista.add(iterator.next());
            }
        }
        Leilao[] array = lista.toArray(new Leilao[0]);
        JList<Leilao> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new LeilaoListarCellRenderer());
        return jlist;
    }

    private JList<Leilao> iteratorLeilaoCadastradoToJList(int LOGADO_OU_SISTEMA) {

        Iterator<Leilao> iterator = facade.listarLeiloesCadastrados();
        atualizarTempo();
        LinkedList<Leilao> lista = new LinkedList<>();
        Usuario user = facade.getLogado();

        if (iterator == null) {
            return new JList<>();
        }
        if (LOGADO_OU_SISTEMA == LOGADO) {

            while (iterator.hasNext()) {
                Leilao leilao = iterator.next();
                if (leilao.getVendedor() == user) {
                    lista.add(leilao);
                }
            }
        }
        if (LOGADO_OU_SISTEMA == SISTEMA) {
            while (iterator.hasNext()) {
                lista.add(iterator.next());
            }
        }
        Leilao[] array = lista.toArray(new Leilao[0]);
        JList<Leilao> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new LeilaoListarCellRenderer());
        return jlist;
    }

    private JList<Leilao> iteratorLeilaoEncerradoToJList(int LOGADO_OU_SISTEMA) {

        Iterator<Leilao> iterator = facade.listarLeiloesEncerrados();
        atualizarTempo();
        LinkedList<Leilao> lista = new LinkedList<>();
        Usuario user = facade.getLogado();

        if (iterator == null) {
            return new JList<>();
        }
        if (LOGADO_OU_SISTEMA == LOGADO) {

            while (iterator.hasNext()) {
                Leilao leilao = iterator.next();
                if (leilao.getVendedor() == user) {
                    lista.add(leilao);
                }
            }
        }
        if (LOGADO_OU_SISTEMA == SISTEMA) {
            while (iterator.hasNext()) {
                lista.add(iterator.next());
            }
        }
        Leilao[] array = lista.toArray(new Leilao[0]);
        JList<Leilao> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new LeilaoListarCellRenderer());
        return jlist;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Usuario
        JMenu menuUsuario = new JMenu("Usuario");
        JMenuItem itemLogar = new JMenuItem("Fazer Login");
        JMenuItem itemCadastrar = new JMenuItem("Cadastrar Usuario");
        menuUsuario.add(itemLogar);
        menuUsuario.add(itemCadastrar);

        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSalvar = new JMenuItem("Salvar");
        JMenuItem itemCarregar = new JMenuItem("Carregar");
        menuArquivo.add(itemSalvar);
        menuArquivo.add(itemCarregar);

        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemDev = new JMenuItem("Dev");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        JMenuItem itemAjuda = new JMenuItem("Ajuda");
        menuAjuda.add(itemSobre);
        menuAjuda.add(itemDev);
        menuAjuda.add(itemAjuda);

        // Adicionar menus à barra de menu
        menuBar.add(menuUsuario);
        menuBar.add(menuArquivo);
        menuBar.add(menuAjuda);

        // Configurar acoes dos itens de menu
        configureMenuUsuarioActions(itemLogar, itemCadastrar);
        configureMenuArquivoActions(itemSalvar, itemCarregar);
        configureMenuAjudaActions(itemSobre, itemDev, itemAjuda);

        return menuBar;
    }

    //Arquivo
    private void configureMenuArquivoActions(JMenuItem itemSalvar, JMenuItem itemCarregar) {

        itemSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarDados();
            }
        });
        itemCarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarDados();
            }
        });
    }

    private void carregarDados() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carregar Dados");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showOpenDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            String diretorio = fileToOpen.getParent();
            String arquivo = fileToOpen.getName();

            if (!arquivo.toLowerCase().endsWith(".cb")) {
                JOptionPane.showMessageDialog(frame, "Por favor, selecione um arquivo com a extensao .cb", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            facade.carregarDados(diretorio, arquivo);
            JOptionPane.showMessageDialog(frame, "Dados carregados com sucesso!");
        }
    }

    private void salvarDados() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Dados");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String diretorio = fileToSave.getParent();
            String arquivo = fileToSave.getName();

            if (!arquivo.toLowerCase().endsWith(".cb")) {
                arquivo += ".cb";
            }

            facade.salvarDados(diretorio, arquivo);
            JOptionPane.showMessageDialog(frame, "Dados salvos com sucesso!");
        }
    }

    //Usuario
    private void configureMenuUsuarioActions(JMenuItem itemLogar, JMenuItem itemCadastrar) {
        itemLogar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginDialog();
            }
        });

        itemCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCadastroDialog();
            }
        });
    }

    private void showCadastroDialog() {
        JDialog cadastroDialog = new JDialog(frame, "Cadastrar Usuario", true);
        cadastroDialog.setSize(600, 400);
        cadastroDialog.setLayout(new GridLayout(7, 2));

        JLabel labelLogin = new JLabel("Login:");
        JTextField textLogin = new JTextField();

        JLabel labelNome = new JLabel("Nome:");
        JTextField textNome = new JTextField();

        JLabel labelSenha = new JLabel("Senha:");
        JPasswordField textSenha = new JPasswordField();

        JLabel labelCpf = new JLabel("CPF:");
        JTextField textCpf = new JTextField();

        JLabel labelEndereco = new JLabel("Endereco:");
        JTextField textEndereco = new JTextField();

        JLabel labelTelefone = new JLabel("Telefone:");
        JTextField textTelefone = new JTextField();

        JButton buttonOk = new JButton("OK");
        JButton buttonFechar = new JButton("Fechar");
        buttonOk.setBackground(new Color(34, 139, 34));
        buttonFechar.setBackground(new Color(189, 34, 34));

        cadastroDialog.add(labelLogin);
        cadastroDialog.add(textLogin);
        cadastroDialog.add(labelNome);
        cadastroDialog.add(textNome);
        cadastroDialog.add(labelSenha);
        cadastroDialog.add(textSenha);
        cadastroDialog.add(labelCpf);
        cadastroDialog.add(textCpf);
        cadastroDialog.add(labelEndereco);
        cadastroDialog.add(textEndereco);
        cadastroDialog.add(labelTelefone);
        cadastroDialog.add(textTelefone);
        cadastroDialog.add(buttonOk);
        cadastroDialog.add(buttonFechar);

        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = textLogin.getText();
                String nome = textNome.getText();
                String senha = new String(textSenha.getPassword());
                String cpf = textCpf.getText();
                String endereco = textEndereco.getText();
                String telefone = textTelefone.getText();
                try {
                    facade.cadastrarUsuario(login, nome, senha, cpf, endereco, telefone);
                    JOptionPane.showMessageDialog(frame, "Usuario cadastrado com sucesso!");
                    cadastroDialog.dispose();
                } catch (UsuarioNaoCadastrouException ex) {
                    JOptionPane.showMessageDialog(frame, "Falha ao cadastrar usuario: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastroDialog.dispose();
            }
        });

        cadastroDialog.setLocationRelativeTo(frame);
        cadastroDialog.setVisible(true);
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(frame, "Fazer Login", true);
        loginDialog.setSize(400, 300);
        loginDialog.setLayout(new GridLayout(3, 2));

        JLabel labelLogin = new JLabel("Login:");
        JTextField textLogin = new JTextField();

        JLabel labelSenha = new JLabel("Senha:");
        JPasswordField textSenha = new JPasswordField();

        JButton buttonOk = new JButton("OK");
        JButton buttonFechar = new JButton("Fechar");
        buttonOk.setBackground(new Color(34, 139, 34));
        buttonFechar.setBackground(new Color(189, 34, 34));

        loginDialog.add(labelLogin);
        loginDialog.add(textLogin);
        loginDialog.add(labelSenha);
        loginDialog.add(textSenha);
        loginDialog.add(buttonOk);
        loginDialog.add(buttonFechar);

        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = textLogin.getText();
                String senha = new String(textSenha.getPassword());
                try {
                    facade.fazerLogin(login, senha);
                    atualizarTempo();
                    JOptionPane.showMessageDialog(frame, "Login realizado com sucesso!");
                    botaoUsuario.setText("Usuario Conectado: " + facade.cb.getLogado().getNome());
                    botaoLeilao.setText("Clique aqui para ver o leilao selecionado");
                    loginDialog.dispose();
                } catch (LoginFalhouException ex) {
                    JOptionPane.showMessageDialog(frame, "Login falhou: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginDialog.dispose();
            }
        });

        loginDialog.setLocationRelativeTo(frame);
        loginDialog.setVisible(true);
    }

    private void configureMenuAjudaActions(JMenuItem itemSobre, JMenuItem itemDev, JMenuItem itemAjuda) {
        itemDev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Esse PBL foi desenvolvido por Pedro Henrique de Araujo Silva\n" +
                        "Estudante da UEFS em 2024.1 e como sua primeira experiencia com interfaces graficas");
            }
        });

        itemSobre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Bazar PBL - Versao 1.0 \n" +
                        "Esse programa foi criado como a conclusao do segundo PBL de Algoritimos 2 da UEFS, semestre 2024.1");
            }
        });
        itemAjuda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Modo de uso do Bazar PBL - Versao 1.0 \n" +
                        "O menu superior indica as manipuacoes do sistema e primeiro passos a se fazer antes de usar o programa\n" +
                        "Os botoes da parte superior da tela servem para operar o leilao selecionado, ver seu perfil de usuario e atualizar o tempo\n" +
                        "Os botoes da esquerda da tela servem para criacao e selecao de leiloes");
            }
        });
    }

    private void showUsuarioInfoDialog(Usuario usuario) {
        JDialog usuarioInfoDialog = new JDialog(frame, "Informacoes do Usuario", false);
        usuarioInfoDialog.setSize(400, 300);
        usuarioInfoDialog.setLayout(new GridLayout(5, 2));

        JLabel labelLogin = new JLabel("Login:");
        JLabel labelLoginValue = new JLabel(usuario.getLogin());

        JLabel labelNome = new JLabel("Nome:");
        JLabel labelNomeValue = new JLabel(usuario.getNome());

        JLabel labelCpf = new JLabel("CPF:");
        JLabel labelCpfValue = new JLabel(usuario.getCpf());

        JLabel labelEndereco = new JLabel("Endereco:");
        JLabel labelEnderecoValue = new JLabel(usuario.getEndereco());

        JLabel labelTelefone = new JLabel("Telefone:");
        JLabel labelTelefoneValue = new JLabel(usuario.getTelefone());

        usuarioInfoDialog.add(labelLogin);
        usuarioInfoDialog.add(labelLoginValue);
        usuarioInfoDialog.add(labelNome);
        usuarioInfoDialog.add(labelNomeValue);
        usuarioInfoDialog.add(labelCpf);
        usuarioInfoDialog.add(labelCpfValue);
        usuarioInfoDialog.add(labelEndereco);
        usuarioInfoDialog.add(labelEnderecoValue);
        usuarioInfoDialog.add(labelTelefone);
        usuarioInfoDialog.add(labelTelefoneValue);

        usuarioInfoDialog.setLocationRelativeTo(frame);
        usuarioInfoDialog.setVisible(true);
    }

    private void showLeiloesTempoDialog(){
        JDialog leiloesTempoDialog = new JDialog(frame, "Buscar leiloes por tempo", true);
        leiloesTempoDialog.setSize(400, 300);
        leiloesTempoDialog.setLayout(new GridLayout(3, 2));

        JLabel labelInicio = new JLabel("Inicio:");
        JSpinner spinnerInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorInicio = new JSpinner.DateEditor(spinnerInicio, "dd/MM/yyyy HH:mm");
        spinnerInicio.setEditor(dateEditorInicio);
        JLabel labelTermino = new JLabel("Termino:");
        JSpinner spinnerTermino = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditorTermino = new JSpinner.DateEditor(spinnerTermino, "dd/MM/yyyy HH:mm");
        spinnerTermino.setEditor(dateEditorTermino);
        JButton buttonBuscar = new JButton("Buscar");
        JButton buttonFechar = new JButton("Fechar");

        buttonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date inicio = (Date) spinnerInicio.getValue();
                Date termino = (Date) spinnerTermino.getValue();
                try {
                    Iterator<Leilao> i = facade.listarLeiloesTempo(inicio,termino);
                    JList<Leilao> jList = LeilaoToJList(i);
                    atualizarTempo();
                    leiloesTempoDialog.dispose();
                    showLeilaoBuscaDialog(jList);
                } catch (NumberFormatException | LeilaoNaoCadastrouException ex) {
                    JOptionPane.showMessageDialog(frame, "Erro ao cadastrar leilao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonFechar.addActionListener(new FecharDialogAction(leiloesTempoDialog));
        leiloesTempoDialog.add(labelInicio);
        leiloesTempoDialog.add(spinnerInicio);
        leiloesTempoDialog.add(labelTermino);
        leiloesTempoDialog.add(spinnerTermino);
        leiloesTempoDialog.add(buttonBuscar);
        leiloesTempoDialog.add(buttonFechar);

        leiloesTempoDialog.setLocationRelativeTo(frame);
        leiloesTempoDialog.setVisible(true);

    }
    private JList<Leilao> LeilaoToJList(Iterator<Leilao> iterator) {


        LinkedList<Leilao> lista = new LinkedList<>();

        if (iterator == null) {
            return new JList<>();
        }

        while (iterator.hasNext()) {
            lista.add(iterator.next());
        }
        Leilao[] array = lista.toArray(new Leilao[0]);
        JList<Leilao> jlist = new JList<>(array);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new LeilaoListarCellRenderer());

        return jlist;
    }
    private void showLeilaoBuscaDialog(JList<Leilao> jList){
        JDialog leiloesTempoDialog = new JDialog(frame, "Busca de leiloes por tempo", true);
        leiloesTempoDialog.setSize(400, 300);
        leiloesTempoDialog.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JButton buttonSelecionar = new JButton("Selecionar leilao");
        JButton buttonLeilaoInfo = new JButton("Informacoes do leilao");
        JButton buttonFechar = new JButton("Fechar");

        buttonSelecionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(frame, "Nenhum leilao selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    Leilao leilao = facade.selecionarLeilao(jList.getSelectedValue());
                    botaoLeilao.setText("Leilao Selecionado: Leilao de " + leilao.getProduto().getDescricaoDetalhada() + " vendido por " + leilao.getVendedor().getLogin());
                    JOptionPane.showMessageDialog(frame, "Leilao selecionado com sucesso!");
                    leiloesTempoDialog.dispose();
                }
            }
        });

        buttonLeilaoInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(frame, "Nenhum leilao selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    leiloesTempoDialog.dispose();
                    showLeilaoInfoDialog(jList.getSelectedValue(), true, "Oeste");
                }
            }
        });

        buttonFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leiloesTempoDialog.dispose();
                showLeiloesTempoDialog();
            }
        });

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        leiloesTempoDialog.add(new JScrollPane(jList), c);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.33;
        c.weighty = 0.0;
        leiloesTempoDialog.add(buttonSelecionar, c);

        c.gridx = 1;
        leiloesTempoDialog.add(buttonLeilaoInfo, c);

        c.gridx = 2;
        leiloesTempoDialog.add(buttonFechar, c);

        leiloesTempoDialog.setLocationRelativeTo(frame);
        leiloesTempoDialog.setVisible(true);
    }

    public static void configurarJDialog(JDialog dialog, JLabel[] labels, JButton[] buttons) {
        if (labels.length != buttons.length) {
            throw new IllegalArgumentException("Labels e Botoões deve ter o mesmo tamanho");
        }

        for (int i = 0; i < labels.length; i++) {
            dialog.add(labels[i]);
            if (buttons[i] == null) {
                dialog.add(new JLabel());
            } else {
                dialog.add(buttons[i]);
            }
        }
    }
    private double obterValorLance() {
        String input = JOptionPane.showInputDialog(frame, "Insira o valor do lance:", "Valor do Lance", JOptionPane.QUESTION_MESSAGE);
        double valorLance = 0.00;
        try {
            valorLance = Double.parseDouble(input);
            if (valorLance <= 0.00) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Valor invalido. Insira um numero positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            valorLance = obterValorLance();
        } catch (NullPointerException e) {
            // Usuario cancelou a entrada
            valorLance = -1.00; // Sinaliza que o valor foi cancelado
        }
        return valorLance;
    }

    // Classes internas
    private class ShowParticipantesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showJListJDialog(iteratorUsuarioToJList(),"Participantes");
        }
    }

    private class ShowLancesAction implements ActionListener {
        private boolean abrirEnvelopes;

        public ShowLancesAction(boolean abrirEnvelopes) {
            this.abrirEnvelopes = abrirEnvelopes;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JList<Lance> jListLances = iteratorLanceToJList(abrirEnvelopes);
                showJListJDialog(jListLances,"Lances");
            } catch (LeilaoNaoEncerradoException ex) {
                JOptionPane.showMessageDialog(frame, "Leilão não está encerrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class FecharDialogAction implements ActionListener {
        private JDialog dialog;

        public FecharDialogAction(JDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }

    private class LanceMinimoActionListener implements ActionListener {
        private final JFrame frame;

        private final JDialog leilaoInfoDialog;

        public LanceMinimoActionListener(JFrame frame, JDialog leilaoInfoDialog) {
            this.frame = frame;
            this.leilaoInfoDialog = leilaoInfoDialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                facade.darLanceMinimo();
                JOptionPane.showMessageDialog(frame, "Lance mnimo dado com sucesso!");
                atualizarTempo();
                leilaoInfoDialog.dispose();
            } catch (LanceInvalidoException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erro ao dar lance minimo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DarLanceActionListener implements ActionListener {

        private final JFrame frame;
        private final JDialog leilaoInfoDialog;

        public DarLanceActionListener(JFrame frame, JDialog leilaoInfoDialog) {
            this.frame = frame;
            this.leilaoInfoDialog = leilaoInfoDialog;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            double valorLance = obterValorLance();
            if (valorLance >= 0.00) {
                try {
                    if (facade.darLance(valorLance)) {
                        JOptionPane.showMessageDialog(frame, "Lance dado com sucesso!");
                        atualizarTempo();
                        leilaoInfoDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Lance invalido: valor menor que o preco minimo + incremento minimo", "Erro ao dar lance", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (LanceInvalidoException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erro ao dar lance", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }



    // Método principal para iniciar a aplicacao
    public static void main(String[] args) {
        BazarGUI gui = new BazarGUI();



    }
}