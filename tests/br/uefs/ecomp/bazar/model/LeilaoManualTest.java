package br.uefs.ecomp.bazar.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;

import org.junit.Before;
import org.junit.Test;

public class LeilaoManualTest {

	private Usuario u1, u2, u3;
	private Produto p1;	
	private LeilaoManual leilao;
	
	@Before
	public void setUp() throws Exception {
		u1 = new Usuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		u2 = new Usuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		u3 = new Usuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		p1 = new Produto("telefone", "Galaxy S", "Samsung Galaxy S", u1);
		leilao = new LeilaoManual(200.00, 5.00, u1, p1);
	}

	@Test
	public void testEstadosLeilao() {
		assertEquals(Leilao.CADASTRADO, leilao.getStatus());
		
		leilao.iniciar();
		assertEquals(Leilao.INICIADO, leilao.getStatus());

		leilao.encerrar();
		assertEquals(Leilao.ENCERRADO, leilao.getStatus());
	}
	
	@Test
	public void testLancesLeilao() throws LanceInvalidoException{
		leilao.iniciar();
		leilao.cadastrarParticipante(u2);
		leilao.cadastrarParticipante(u3);
		
		leilao.darLanceMinimo(u2);
		Lance l = leilao.getUltimoLance();	
		assertEquals(205.00, l.getValor(), 0.001);
		assertEquals(u2, l.getParticipante());
		
		leilao.darLanceMinimo(u3);
		l = leilao.getUltimoLance();	
		assertEquals(210.00, l.getValor(), 0.001);
		assertEquals(u3, l.getParticipante());
		
		assertFalse(leilao.darLance(u2, 208.00));
		l = leilao.getUltimoLance();
		assertEquals(210.00, l.getValor(), 0.001);
		
		assertFalse(leilao.darLance(u2, 214.00));
		l = leilao.getUltimoLance();
		assertEquals(210.00, l.getValor(), 0.001);
		
		assertTrue(leilao.darLance(u2, 230.00));
		l = leilao.getUltimoLance();	
		assertEquals(230.00, l.getValor(), 0.001);
		assertEquals(u2, l.getParticipante());
	}
	
	@Test
	public void testEncerrarLeilao() throws LanceInvalidoException{
		leilao.iniciar();
		leilao.cadastrarParticipante(u2);
		leilao.cadastrarParticipante(u3);
		leilao.darLanceMinimo(u2);
		leilao.darLanceMinimo(u3);
		
		leilao.encerrar();
		Venda v = leilao.getVenda();
		assertNotNull(v);
		assertEquals(210.00, v.getValor(), 0.001);
		assertEquals(u1, v.getVendedor());
		assertEquals(u3, v.getComprador());
		assertEquals(p1, v.getProduto());
		assertEquals(leilao, v.getLeilao());
	}
}
