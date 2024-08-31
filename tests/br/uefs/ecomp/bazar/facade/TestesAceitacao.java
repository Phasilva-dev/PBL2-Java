package br.uefs.ecomp.bazar.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

import br.uefs.ecomp.bazar.model.Lance;
import br.uefs.ecomp.bazar.model.Leilao;
import br.uefs.ecomp.bazar.model.Produto;
import br.uefs.ecomp.bazar.model.Usuario;
import br.uefs.ecomp.bazar.model.Venda;
import br.uefs.ecomp.bazar.model.exception.LanceInvalidoException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;
import br.uefs.ecomp.bazar.model.exception.LeilaoNaoEncerradoException;
import br.uefs.ecomp.bazar.model.exception.LoginFalhouException;
import br.uefs.ecomp.bazar.model.exception.ProdutoNaoCadastrouException;
import br.uefs.ecomp.bazar.model.exception.UsuarioNaoCadastrouException;

public class TestesAceitacao {
	
	public static final int DELAY = 50;

	BazarFacade f = new BazarFacade();

	@Test
	public void testCadastrarUsuarioCorreto() throws UsuarioNaoCadastrouException {
		Usuario usuario = f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		assertEquals("maria", usuario.getLogin());
		assertEquals("Maria dos Santos", usuario.getNome());
		assertEquals("senha1", usuario.getSenha());
		assertEquals("123456789-01", usuario.getCpf());
		assertEquals("Rua Drummond, 23, Centro", usuario.getEndereco());
		assertEquals("7532213456", usuario.getTelefone());
	}

	@Test(expected = UsuarioNaoCadastrouException.class)
	public void testCadastrarUsuarioJaCadastrado() throws UsuarioNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("maria", "Outro Nome", "senha2", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
	}
	
	@Test(expected = UsuarioNaoCadastrouException.class)
	public void testCadastrarSenhaInvalida() throws UsuarioNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
	}
	
	@Test
	public void testFazerLoginCorreto() 
			throws UsuarioNaoCadastrouException, LoginFalhouException {
		Usuario usuarioCadastrado = f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		Usuario usuarioLogado = f.fazerLogin("maria", "senha1");
		assertNotNull(usuarioLogado);
		assertSame(usuarioCadastrado, usuarioLogado);
	}

	@Test(expected = LoginFalhouException.class)
	public void testLoginErrado() 
			throws UsuarioNaoCadastrouException, LoginFalhouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("mario", "senha1");
	}

	@Test(expected = LoginFalhouException.class)
	public void testSenhaErrada() 
			throws UsuarioNaoCadastrouException, LoginFalhouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senhaerrada");
	}

	@Test
	public void testCadastrarProdutoCorreto() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, ProdutoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		
		// cadastra produtos do usuario logado
		Produto produto = f.cadastrarProduto("telefone", "Galaxy S", "Samsung Galaxy S");
		assertNotNull(produto);
		assertEquals("telefone", produto.getTipo());
		assertEquals("Galaxy S", produto.getDescricaoResumida());
		assertEquals("Samsung Galaxy S", produto.getDescricaoDetalhada());
	}

	@Test
	public void testCadastrarProdutoIncorreto() 
			throws ProdutoNaoCadastrouException, UsuarioNaoCadastrouException, LoginFalhouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		try {
			f.cadastrarProduto("", "Galaxy S", "Samsung Galaxy S");
			fail("Tipo do produto n?o cadastrado.");
		} catch (ProdutoNaoCadastrouException e) { }
		try {
			f.cadastrarProduto("telefone", "", "Samsung Galaxy S");
			fail("Descricao resumida do produto n?o cadastrada.");
		} catch (ProdutoNaoCadastrouException e) { }
	}
	
	@Test
	public void testCadastrarLeilaoManualCorreto() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, 
			ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		Usuario vendedor = f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Leilao leilao = f.cadastrarLeilaoManual(produto, 200.00, 5.00);
		assertNotNull(leilao);
		assertEquals(200.00, leilao.getPrecoMinimo(), 0.001);
		assertEquals(5.00, leilao.getIncrementoMinimo(), 0.001);
		assertSame(produto, leilao.getProduto());
		assertSame(vendedor, leilao.getVendedor());
		assertEquals(Leilao.CADASTRADO, leilao.getStatus());
		assertNull(leilao.getUltimoLance());
		assertNull(leilao.getVenda());
	}

	@Test
	public void testCadastrarLeilaoManualErrado() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, 
			ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		
		try {
			f.cadastrarLeilaoManual(produto, 200.00, 0.00);
			fail("Incremento minimo deve ser maior que zero");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoManual(produto, 0.00, 5.00);
			fail("Preco minimo deve ser maior que zero");
		} catch (LeilaoNaoCadastrouException e) { }
	}
	
	@Test
	public void testIniciarLeilao() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Leilao leilao = f.cadastrarLeilaoManual(produto, 200.00, 5.00);
		f.iniciarLeilao(leilao);
		assertEquals(Leilao.INICIADO, leilao.getStatus());
		assertTrue(Calendar.getInstance().getTimeInMillis() - leilao.getInicio().getTime() < DELAY);
	}

	@Test
	public void testListarLeiloesIniciadosPorUmSoUsuario() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, InterruptedException {
		
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Produto p2 = f.cadastrarProduto("telefone", "Galaxy S", "Samsung Galaxy S");
		Produto p3 = f.cadastrarProduto("tablet", "iPad 2", "Apple iPad 2");
		Leilao l1 = f.cadastrarLeilaoManual(p1, 200.00, 5.00);
		Leilao l2 = f.cadastrarLeilaoManual(p2, 300.00, 5.00);
		Leilao l3 = f.cadastrarLeilaoManual(p3, 1000.00, 10.00);
		f.iniciarLeilao(l3);
		Thread.sleep(5);
		f.iniciarLeilao(l1);
		
		Iterator it = f.listarLeiloesIniciados();
		assertTrue(it.hasNext());
		assertSame(l3, it.next());
		assertSame(l1, it.next());
		assertFalse(it.hasNext());
	}

	@Test
	public void testListarLeiloesIniciadosPorMaisDeUmUsuario() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, InterruptedException {
		
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		f.cadastrarUsuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Leilao l1 = f.cadastrarLeilaoManual(p1, 200.00, 5.00);
		
		f.fazerLogin("joao", "senha2");
		Produto p3 = f.cadastrarProduto("tablet", "iPad 2", "Apple iPad 2");
		Leilao l3 = f.cadastrarLeilaoManual(p3, 1000.00, 10.00);
		f.iniciarLeilao(l3);
		Thread.sleep(5);
		
		f.fazerLogin("maria", "senha1");
		f.iniciarLeilao(l1);
		
		Iterator it = f.listarLeiloesIniciados();
		assertTrue(it.hasNext());
		assertSame(l3, it.next());
		assertSame(l1, it.next());
		assertFalse(it.hasNext());
	}

	@Test
	public void testParticiparLeilaoDarLancesMinimos() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, 
		InterruptedException, LanceInvalidoException {
		
		// Como o sistema ainda nao eh multi-usuario, cada usuario 
		// deve fazer login depois de acoes de outros usuarios.
		// Por enquanto, cada usuario so participa de um leilao de cada vez.

		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		f.cadastrarUsuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Leilao leilao = f.cadastrarLeilaoManual(p1, 200.00, 5.00);
		f.iniciarLeilao(leilao);
		
		Usuario u2 = f.fazerLogin("joao", "senha2");
		f.participarLeilao(leilao);
		f.darLanceMinimo();
		Lance ultimoLance = leilao.getUltimoLance();
		assertEquals(205.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		Usuario u3 = f.fazerLogin("pedro", "senha3");
		f.participarLeilao(leilao);
		f.darLanceMinimo();
		ultimoLance = leilao.getUltimoLance();
		assertEquals(210.00, ultimoLance.getValor(), 0.001);
		assertEquals(u3, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		f.fazerLogin("joao", "senha2");
		f.darLanceMinimo();
		ultimoLance = leilao.getUltimoLance();
		assertEquals(215.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);

		f.fazerLogin("pedro", "senha3");
		f.darLanceMinimo();
		ultimoLance = leilao.getUltimoLance();
		assertEquals(220.00, ultimoLance.getValor(), 0.001);
		assertEquals(u3, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
	}

	@Test
	public void testParticiparLeilaoDarLancesNormais() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, 
		InterruptedException, LanceInvalidoException {
		
		// Como o sistema ainda nao eh multi-usuario, cada usuario 
		// deve fazer login depois de acoes de outros usuarios.
		// Por enquanto, cada usuario so participa de um leilao de cada vez.

		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		f.cadastrarUsuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Leilao leilao = f.cadastrarLeilaoManual(p1, 200.00, 5.00);
		f.iniciarLeilao(leilao);
		
		Usuario u2 = f.fazerLogin("joao", "senha2");
		f.participarLeilao(leilao);
		assertFalse("Lance deve ser maior o preco minimo mais o incremento minimo.", f.darLance(195.00));
		f.darLance(220.00);
		Lance ultimoLance = leilao.getUltimoLance();
		assertEquals(220.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		Usuario u3 = f.fazerLogin("pedro", "senha3");
		f.participarLeilao(leilao);
		assertFalse("Lance deve ser maior o ultimo lance mais o incremento minimo.", f.darLance(224.00));
		f.darLance(230.00);
		ultimoLance = leilao.getUltimoLance();
		assertEquals(230.00, ultimoLance.getValor(), 0.001);
		assertEquals(u3, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		f.fazerLogin("joao", "senha2");
		f.darLance(250.00);
		ultimoLance = leilao.getUltimoLance();
		assertEquals(250.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);

		f.fazerLogin("pedro", "senha3");
		f.darLance(300.00);
		ultimoLance = leilao.getUltimoLance();
		assertEquals(300.00, ultimoLance.getValor(), 0.001);
		assertEquals(u3, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
	}
	
	@Test
	public void testEncerrarLeilao() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, 
		InterruptedException, LanceInvalidoException {
		
		// Como o sistema ainda nao eh multi-usuario, cada usuario 
		// deve fazer login depois de acoes de outros usuarios.
		// Por enquanto, cada usuario so participa de um leilao de cada vez.

		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		f.cadastrarUsuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Leilao leilao = f.cadastrarLeilaoManual(p1, 200.00, 5.00);
		f.iniciarLeilao(leilao);
		
		Usuario u2 = f.fazerLogin("joao", "senha2");
		f.participarLeilao(leilao);
		f.darLanceMinimo();
		Lance ultimoLance = leilao.getUltimoLance();
		assertEquals(205.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		Usuario u3 = f.fazerLogin("pedro", "senha3");
		f.participarLeilao(leilao);
		f.darLance(220.00);
		ultimoLance = leilao.getUltimoLance();
		assertEquals(220.00, ultimoLance.getValor(), 0.001);
		assertEquals(u3, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		f.fazerLogin("joao", "senha2");
		f.darLance(300.00);
		ultimoLance = leilao.getUltimoLance();
		assertEquals(300.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		f.fazerLogin("maria", "senha1");
		f.encerrarLeilao();
		assertEquals(Leilao.ENCERRADO, leilao.getStatus());
		assertTrue(Calendar.getInstance().getTimeInMillis() - leilao.getTermino().getTime() < DELAY);
		
		Venda venda = leilao.getVenda();
		assertNotNull(venda);
		assertSame(leilao.getProduto(), venda.getProduto());
		assertSame(leilao.getVendedor(), venda.getVendedor());
		assertSame(u2, venda.getComprador());
		assertSame(leilao.getUltimoLance().getParticipante(), venda.getComprador());
		assertEquals(300.00, venda.getValor(), 0.001);
		assertSame(leilao, venda.getLeilao());
	}
	
	@Test
	public void testCadastrarLeilaoAutomaticoCorreto() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, 
			ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		Usuario vendedor = f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Calendar inicio = Calendar.getInstance();
		inicio.set(2024, Calendar.OCTOBER, 20, 9, 30);
		Calendar termino = Calendar.getInstance();
		termino.set(2024, Calendar.OCTOBER, 20, 11, 30);
		
		Leilao leilao = f.cadastrarLeilaoAutomatico(produto, 200.00, 5.00, inicio.getTime(), termino.getTime());
		assertNotNull(leilao);
		assertEquals(200.00, leilao.getPrecoMinimo(), 0.001);
		assertEquals(5.00, leilao.getIncrementoMinimo(), 0.001);
		assertEquals(inicio.getTime(), leilao.getInicio());
		assertEquals(termino.getTime(), leilao.getTermino());
		assertSame(produto, leilao.getProduto());
		assertSame(vendedor, leilao.getVendedor());
		assertEquals(Leilao.CADASTRADO, leilao.getStatus());
		assertNull(leilao.getUltimoLance());
		assertNull(leilao.getVenda());
	}

	@Test
	public void testCadastrarLeilaoAutomaticoErrado() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, 
			ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Calendar inicio = Calendar.getInstance();
		inicio.set(2024, 10, 20, 9, 30);
		Calendar termino = Calendar.getInstance();
		termino.set(2024, 10, 20, 11, 30);
		
		Calendar terminoErrado = Calendar.getInstance();
		terminoErrado.set(2024, 10, 20, 9, 29);
		
		Calendar inicioErrado = Calendar.getInstance();
		inicioErrado.add(Calendar.HOUR_OF_DAY, -1);
		
		try {
			f.cadastrarLeilaoAutomatico(produto, 200.00, 0.00, inicio.getTime(), termino.getTime());
			fail("Incremento minimo deve ser maior que zero.");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoAutomatico(produto, 0.00, 5.00, inicio.getTime(), termino.getTime());
			fail("Preco minimo deve ser maior que zero.");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoAutomatico(produto, 200.00, 5.00, inicio.getTime(), terminoErrado.getTime());
			fail("Momento de termino do leilao deve ocorrer apos momento de inicio.");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoAutomatico(produto, 200.00, 5.00, inicioErrado.getTime(), termino.getTime());
			fail("Momento de inicio do leilao deve ocorrer apos a hora atual.");
		} catch (LeilaoNaoCadastrouException e) { }
	}
	
	@Test
	public void testCadastrarLeilaoAutomaticoFechadoCorreto() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, 
			ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		Usuario vendedor = f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Calendar inicio = Calendar.getInstance();
		inicio.set(2024, 10, 20, 9, 30);
		Calendar termino = Calendar.getInstance();
		termino.set(2024, 10, 20, 11, 30);
		
		Leilao leilao = f.cadastrarLeilaoAutomaticoFechado(produto, 200.00, 5.00, inicio.getTime(), termino.getTime());
		assertNotNull(leilao);
		assertEquals(200.00, leilao.getPrecoMinimo(), 0.001);
		assertEquals(5.00, leilao.getIncrementoMinimo(), 0.001);
		assertEquals(inicio.getTime(), leilao.getInicio());
		assertEquals(termino.getTime(), leilao.getTermino());
		assertSame(produto, leilao.getProduto());
		assertSame(vendedor, leilao.getVendedor());
		assertEquals(Leilao.CADASTRADO, leilao.getStatus());
		assertNull(leilao.getUltimoLance());
		assertNull(leilao.getVenda());
	}

	@Test
	public void testCadastrarLeilaoAutomaticoFechadoErrado() 
			throws UsuarioNaoCadastrouException, LoginFalhouException, 
			ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.fazerLogin("maria", "senha1");
		Produto produto = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Calendar inicio = Calendar.getInstance();
		inicio.set(2024, 10, 20, 9, 30);
		Calendar termino = Calendar.getInstance();
		termino.set(2024, 10, 20, 11, 30);
		
		Calendar terminoErrado = Calendar.getInstance();
		terminoErrado.set(2024, 10, 20, 9, 29);
		
		Calendar inicioErrado = Calendar.getInstance();
		inicioErrado.add(Calendar.HOUR_OF_DAY, -1);
		
		try {
			f.cadastrarLeilaoAutomaticoFechado(produto, 200.00, 0.00, inicio.getTime(), termino.getTime());
			fail("Incremento minimo deve ser maior que zero.");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoAutomaticoFechado(produto, 0.00, 5.00, inicio.getTime(), termino.getTime());
			fail("Preco minimo deve ser maior que zero.");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoAutomaticoFechado(produto, 200.00, 5.00, inicio.getTime(), terminoErrado.getTime());
			fail("Momento de termino do leilao deve ocorrer apos momento de inicio.");
		} catch (LeilaoNaoCadastrouException e) { }
		try {
			f.cadastrarLeilaoAutomaticoFechado(produto, 200.00, 5.00, inicioErrado.getTime(), termino.getTime());
			fail("Momento de inicio do leilao deve ocorrer apos a hora atual.");
		} catch (LeilaoNaoCadastrouException e) { }
	}
	
	@Test
	public void testParticiparLeilaoAutomaticoFechadoDarLances() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, 
		InterruptedException, LanceInvalidoException {
		
		// Como o sistema ainda nao eh multi-usuario, cada usuario 
		// deve fazer login depois de acoes de outros usuarios.
		// Por enquanto, cada usuario so participa de um leilao de cada vez.

		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		f.cadastrarUsuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		f.cadastrarUsuario("marcos", "Marcos Oliveira", "senha4", "123123123-01", "Rua Quintana, 45, Santa Monica", "7532213221");
		
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Calendar inicio = Calendar.getInstance();
		inicio.add(Calendar.SECOND, +1);
		Calendar termino = Calendar.getInstance();
		termino.add(Calendar.SECOND, +2);

		Leilao leilao = f.cadastrarLeilaoAutomaticoFechado(p1, 200.00, 5.00, inicio.getTime(), termino.getTime());
		
		//f.iniciarLeilao(leilao); // no futuro, devera ser feito automaticamente, observando o momento de inicio.

		Usuario u2 = f.fazerLogin("joao", "senha2");
		f.participarLeilao(leilao);
		try {
			f.darLance(220.00);
			fail("Leilao nao esta ativo ainda.");
		} catch (LanceInvalidoException e) { }

		Thread.sleep(1000);
		assertFalse("Lance deve ser maior o preco minimo mais o incremento minimo.", f.darLance(195.00));
		f.darLance(220.00);
		Lance ultimoLance = leilao.getUltimoLance();
		assertEquals(220.00, ultimoLance.getValor(), 0.001);
		assertEquals(u2, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		
		Usuario u3 = f.fazerLogin("pedro", "senha3");
		f.participarLeilao(leilao);
		f.darLanceMinimo();
		ultimoLance = leilao.getUltimoLance();
		assertEquals(205.00, ultimoLance.getValor(), 0.001);
		assertEquals(u3, ultimoLance.getParticipante());
		assertTrue(Calendar.getInstance().getTimeInMillis() - ultimoLance.getMomento().getTime() < DELAY);
		Thread.sleep(2 * DELAY);
		
		f.fazerLogin("joao", "senha2");
		try {
			f.darLance(250.00);
			fail("Participante ja deu seu lance.");
		} catch (LanceInvalidoException e) { }

		f.fazerLogin("marcos", "senha4");
		f.participarLeilao(leilao);
		Thread.sleep(1000);	
		try {
			f.darLance(300.00);
			fail("Leilao ja nao esta mais ativo.");
		} catch (LanceInvalidoException e) { }
	}	

	@Test
	public void testAbrirEnvelopesLeilaoAutomaticoFechado() 
		throws UsuarioNaoCadastrouException, LoginFalhouException, 
		ProdutoNaoCadastrouException, LeilaoNaoCadastrouException, 
		InterruptedException, LanceInvalidoException, 
		LeilaoNaoEncerradoException {
		
		// Como o sistema ainda nao eh multi-usuario, cada usuario 
		// deve fazer login depois de acoes de outros usuarios.
		// Por enquanto, cada usuario so participa de um leilao de cada vez.

		f.cadastrarUsuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		f.cadastrarUsuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
		f.cadastrarUsuario("pedro", "Pedro da Silva", "senha3", "456789123-01", "Rua Andrade, 45, Cidade Nova", "7532217890");
		f.cadastrarUsuario("marcos", "Marcos Oliveira", "senha4", "123123123-01", "Rua Quintana, 45, Santa Monica", "7532213221");
		
		f.fazerLogin("maria", "senha1");
		Produto p1 = f.cadastrarProduto("telefone", "iPhone 4S", "Apple iPhone 4S");
		Calendar inicio = Calendar.getInstance();
		inicio.add(Calendar.SECOND, +1);
		Calendar termino = Calendar.getInstance();
		termino.add(Calendar.SECOND, +2);
		Leilao leilao = f.cadastrarLeilaoAutomaticoFechado(p1, 200.00, 5.00, inicio.getTime(), termino.getTime());

		Thread.sleep(1000); // aguarda 1 segundo ate o leilao iniciar

		Thread.sleep(2 * DELAY);

		f.fazerLogin("joao", "senha2");
		f.participarLeilao(leilao);
		f.darLance(250.00);
		
		Thread.sleep(2 * DELAY);
		f.fazerLogin("pedro", "senha3");
		f.participarLeilao(leilao);
		f.darLance(300.00);
	
		Thread.sleep(2 * DELAY);
		f.fazerLogin("marcos", "senha4");
		f.participarLeilao(leilao);
		f.darLance(270.00);
		
		try {
			f.abrirEnvelopesLeilaoAutomaticoFechado();
			fail("Leilao ainda nao foi encerrado.");
		} catch (LeilaoNaoEncerradoException e) { }

		Thread.sleep(1000);
		Iterator lances = f.abrirEnvelopesLeilaoAutomaticoFechado();
		assertNotNull(lances);
	}	

	@Test
	public void testListarMomentoAtual() {
		Date momentoAtual = Calendar.getInstance().getTime();
		assertTrue(momentoAtual.getTime() - f.listarMomentoAtual().getTime() < DELAY);
	}
}
