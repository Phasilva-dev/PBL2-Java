package br.uefs.ecomp.bazar.model;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ProdutoTest {

	@Test
	public void testCriarProduto() {
		Usuario vendedor = new Usuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
		Produto p = new Produto("telefone", "Galaxy S", "Samsung Galaxy S", vendedor);
		assertFalse(p.isVendido());
	}

}
