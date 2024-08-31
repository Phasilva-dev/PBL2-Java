package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class LeilaoAutomaticoTest {

    private Usuario u1,u2,u3,u4;
    private Produto p1;
    private LeilaoAutomatico leilao;
    private Date inicio, termino;

    @Before
    public void setUp() throws Exception {
        u1 = new Usuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
        u2 = new Usuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
        u3 = new Usuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
        u4 = new Usuario("mario", "Mario da Silva", "senha4", "456789123-02", "Rua Andrade, 46, Parque Ipe", "7532217833");
        inicio = new Date(System.currentTimeMillis() + 1000); // 1 segundo no futuro
        termino = new Date(System.currentTimeMillis() + 2000); // 2 segundos no futuro
        p1 = new Produto("telefone", "Galaxy S", "Samsung Galaxy S", u1);
        leilao = new LeilaoAutomatico(p1, 100.0, 10.0, u1, inicio, termino);
    }

    @Test
    public void inicializarLeilaoAutomaticoTest() throws LeilaoNaoCadastrouException {
        Date inicio = new Date(System.currentTimeMillis() + 1000);
        Date termino = new Date(System.currentTimeMillis() + 2000);
        leilao = new LeilaoAutomatico(p1,200.00, 5.00, u1, inicio, termino);
        assertEquals(200, leilao.getPrecoMinimo(), 0.0001);
        assertEquals(5.00, leilao.getIncrementoMinimo(), 0.0001);
        assertEquals(u1, leilao.getVendedor());
        assertEquals(p1, leilao.getProduto());
        assertEquals(inicio, leilao.getInicio());
        assertEquals(termino, leilao.getTermino());
        assertEquals(Leilao.CADASTRADO, leilao.getStatus());
    }

    @Test
    public void testLeilaoCadastroErrado() throws LeilaoNaoCadastrouException {

        inicio = new Date(System.currentTimeMillis() + 2000);
        termino = new Date(System.currentTimeMillis() + 1000);
        try {
            new LeilaoAutomatico(p1, 100.0, 10.0, u1, inicio, termino);
            fail("Inicio apos o termino");
        } catch (LeilaoNaoCadastrouException e) { }
    }

    @Test
    public void testCheckInicioTermino() throws LeilaoNaoCadastrouException {
        inicio = new Date(System.currentTimeMillis() + 1000);
        termino = new Date(System.currentTimeMillis() + 3000);
        leilao = new LeilaoAutomatico(p1, 100.0, 10.0, u1, inicio, termino);

        Date antesDoInicio = new Date(System.currentTimeMillis() - 1000);
        Date depoisDoInicio = new Date(System.currentTimeMillis() + 2000);

        assertFalse(leilao.check_inicio(antesDoInicio));
        assertTrue(leilao.check_inicio(depoisDoInicio));

        Date antesDoTermino = new Date(System.currentTimeMillis() + 1000);
        Date depoisDoTermino = new Date(System.currentTimeMillis() + 4000);

        assertFalse(leilao.check_termino(antesDoTermino));
        assertTrue(leilao.check_termino(depoisDoTermino));
    }

    @Test
    public void testDarLance() throws LanceInvalidoException,InterruptedException{

        assertEquals(leilao.getStatus(), Leilao.CADASTRADO);
        Thread.sleep(1000);
        try {
            leilao.darLanceMinimo(u4);
            fail("Leilão ainda nao iniciou");
        } catch (LanceInvalidoException e){ }
        leilao.iniciar();
        assertEquals(leilao.getStatus(), Leilao.INICIADO);
        leilao.darLance(u2, 150.0);
        leilao.darLance(u1, 170.0);
        leilao.darLance(u3, 300);
        Thread.sleep(1000);
        leilao.encerrar();
        try {
            leilao.darLance(u4, 200);
            fail("Leilao já encerrado");
        } catch (LanceInvalidoException e){ }
        assertEquals(leilao.getStatus(), Leilao.ENCERRADO);

        Iterator<Lance> i = this.leilao.listarLances();
        assertTrue(i.hasNext());
        Lance l2 = i.next();
        Lance l1 = i.next();
        Lance l3 = i.next();
        assertFalse(i.hasNext());
        assertEquals (u2, l2.getParticipante());
        assertEquals(150.0, l2.getValor(),0.0001);
        assertEquals(u1, l1.getParticipante());
        assertEquals(170, l1.getValor(),0.0001);
        assertEquals (u3, l3.getParticipante());
        assertEquals(300.0, l3.getValor(),0.0001);
    }

}
