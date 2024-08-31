package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LeilaoAutomaticoFechadoTest {
    private Usuario u1, u2, u3, u4;
    private Produto p1;
    private LeilaoAutomaticoFechado leilao;
    private Date inicio,termino;

    @Before
    public void setUp() throws Exception {
        u1 = new Usuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
        u2 = new Usuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
        u3 = new Usuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
        u4 = new Usuario("mario", "Mario da Silva", "senha4", "456789123-02", "Rua Andrade, 46, Parque Ipe", "7532217833");
        p1 = new Produto("telefone", "Galaxy S", "Samsung Galaxy S", u1);
        inicio = new Date(System.currentTimeMillis() + 1000); // 1 segundo no futuro
        termino = new Date(System.currentTimeMillis() + 2000); // 2 segundos no futuro
        leilao = new LeilaoAutomaticoFechado(p1, 100.0, 10.0, u1, inicio, termino);
    }

    @Test
    public void leilaoIniciarEncerrarLanceOrganizarTest() throws InterruptedException, LanceInvalidoException {
        assertEquals(leilao.getStatus(), Leilao.CADASTRADO);
        try {
            leilao.darLanceMinimo(u4);
            fail("Leilão ainda nao iniciou");
        } catch (LanceInvalidoException e){ }
        Thread.sleep(1000);
        leilao.iniciar();
        assertEquals(leilao.getStatus(), Leilao.INICIADO);
        leilao.darLance(u2, 150.0);
        leilao.darLance(u1, 120.0);
        leilao.darLance(u3, 300);
        try {
           leilao.darLance(u2,180);
           fail("Usuario não pode dar dois lances");
        } catch (LanceInvalidoException e) { }
        Thread.sleep(1000);
        leilao.encerrar();
        try {
            leilao.darLance(u4, 200);
            fail("Leilao já encerrado");
        } catch (LanceInvalidoException e){ }
        assertEquals(leilao.getStatus(), Leilao.ENCERRADO);

        leilao.organizarLances();

        List<Map.Entry<Usuario, Lance>> lista = new LinkedList<>(leilao.lances.getLancesMap().entrySet());
        assertEquals (u3, lista.get(0).getKey());
        assertEquals(300.0, lista.get(0).getValue().getValor(),0.0001);
        assertEquals(u2, lista.get(1).getKey());
        assertEquals(150.0, lista.get(1).getValue().getValor(),0.0001);
        assertEquals (u1, lista.get(2).getKey());
        assertEquals(120.0, lista.get(2).getValue().getValor(),0.0001);
    }
}
