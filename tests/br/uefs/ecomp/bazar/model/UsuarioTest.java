package br.uefs.ecomp.bazar.model;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;
import br.uefs.ecomp.bazar.model.exception.ProdutoNaoCadastrouException;


import java.util.Iterator;

public class UsuarioTest extends TestCase {

	private Usuario u1, u2, u3;
	
	@Before
	public void setUp() throws Exception {
		u1 = new Usuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		u2 = new Usuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		u3 = new Usuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
	}
	
	@Test
	public void testCadastrarListarProdutos() throws ProdutoNaoCadastrouException {
		
		Iterator iterador = u1.listarProdutosCadastrados();
		assertFalse(iterador.hasNext());
		
		u1.cadastrarProduto("telefone", "Galaxy S", "Samsung Galaxy S");
		iterador = u1.listarProdutosCadastrados();
		assertTrue(iterador.hasNext());
		
		Produto produto = (Produto) iterador.next();
		assertEquals("telefone", produto.getTipo());
		assertEquals("Galaxy S", produto.getDescricaoResumida());
		assertEquals("Samsung Galaxy S", produto.getDescricaoDetalhada());
		assertFalse(iterador.hasNext());
		
		u1.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		iterador = u1.listarProdutosCadastrados();
		produto = (Produto) iterador.next();
		produto = (Produto) iterador.next();
		assertEquals("telefone", produto.getTipo());
		assertEquals("iPhone 4S", produto.getDescricaoResumida());
		assertEquals("Apple iPhone 4S", produto.getDescricaoDetalhada());
		assertFalse(iterador.hasNext());
		
	}
	
	@Test
	public void testCadastrarIniciarTerminarLeilao() throws ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, 
			LanceInvalidoException {
		u1.cadastrarProduto("telefone", "Galaxy S", "Samsung Galaxy S");
		Iterator iterador = u1.listarProdutosCadastrados();
		Produto produto = (Produto)iterador.next();
		
		Leilao leilao = u1.cadastrarLeilaoManual(200.00, 5.00, produto);
		assertNotNull(leilao);
		u1.iniciarLeilao(leilao);
		Venda venda = u1.encerrarLeilaoAtivo();
		assertNull(venda);
		
		leilao = u1.cadastrarLeilaoManual(150.00, 5.00, produto);
		assertNotNull(leilao);
		u1.iniciarLeilao(leilao);
		u2.participarLeilao(leilao);
		u3.participarLeilao(leilao);
		u2.darLanceMinimo();
		u3.darLanceMinimo();
		venda = u1.encerrarLeilaoAtivo();
		assertNotNull(venda);
		assertSame(venda, leilao.getVenda());
		assertTrue(produto.isVendido());
	}

}
