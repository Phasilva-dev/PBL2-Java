package br.uefs.ecomp.bazar.model;

import br.uefs.ecomp.bazar.model.exception.LeilaoNaoCadastrouException;
import br.uefs.ecomp.bazar.model.exception.ProdutoNaoCadastrouException;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class VendaTest {
    @Test
    public void testGerarVenda() throws ProdutoNaoCadastrouException, LeilaoNaoCadastrouException {
        Usuario vendedor = new Usuario("maria", "Maria dos Santos", "senha1", "123456789-01", "Rua Drummond, 23, Centro", "7532213456");
        Usuario comprador = new Usuario("joao", "Joao dos Santos", "senha2", "987654321-01", "Rua Pessoa, 12, Centro", "7532216543");
        Produto p = new Produto("telefone", "Galaxy S", "Samsung Galaxy S", vendedor);
        Leilao leilao = new LeilaoManual(200.00, 5.00, vendedor, p);
        Venda venda = new Venda(200, comprador, vendedor, p, leilao);
        assertEquals(venda.getValor(), 200, 0.0001);
        assertEquals(venda.getVendedor(), vendedor);
        assertEquals(venda.getComprador(), comprador);
        assertEquals(venda.getProduto(), p);
        assertEquals(venda.getLeilao(), leilao);
        assertNull(venda.getHora_da_venda());
    }

}
