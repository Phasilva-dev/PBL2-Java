/*package br.uefs.ecomp.bazar.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ListaEncadeadaTest {

	ListaEncadeada lista;
	Comparable data1, data2, data3;

	@Before
	public void setUp() throws Exception {
		lista = new ListaEncadeada();
		data1 = new String("Data1");
		data2 = new String("Data2");
		data3 = new String("Data3");
	}

	@Test
	public void testEstaVazia() {
		assertTrue(lista.estaVazia());
	}

	@Test
	public void testTamanho() {
		assertEquals(0, lista.tamanho());

		lista.insereInicio(data1);
		assertEquals(1, lista.tamanho());

		lista.insereInicio(data2);
		lista.insereInicio(data3);
		assertEquals(3, lista.tamanho());

		lista.deleteFinal();
		assertEquals(2, lista.tamanho());

		lista.deleteInicio();
		lista.deleteFinal();
		assertEquals(0, lista.tamanho());
	}

	@Test
	public void testAdicionaInicioRemoveInicio() {
		lista.insereInicio(data1);
		lista.insereInicio(data2);
		lista.insereInicio(data3);
		assertFalse(lista.estaVazia());

		assertSame(data3, lista.deleteInicio());
		assertFalse(lista.estaVazia());

		assertSame(data2, lista.deleteInicio());
		assertFalse(lista.estaVazia());

		assertSame(data1, lista.deleteInicio());
		assertTrue(lista.estaVazia());	
	}

	@Test
	public void testAdicionaInicioRemoveFinal() {
		lista.insereInicio(data1);
		lista.insereInicio(data2);
		lista.insereInicio(data3);
		assertFalse(lista.estaVazia());

		assertSame(data1, lista.deleteFinal());
		assertFalse(lista.estaVazia());

		assertSame(data2, lista.deleteFinal());
		assertFalse(lista.estaVazia());

		assertSame(data3, lista.deleteFinal());
		assertTrue(lista.estaVazia());	
	}

	@Test
	public void testAdicionaFinalRemoveInicio() {
		lista.insereFinal(data1);
		lista.insereFinal(data2);
		lista.insereFinal(data3);
		assertFalse(lista.estaVazia());

		assertSame(data1, lista.deleteInicio());
		assertFalse(lista.estaVazia());

		assertSame(data2, lista.deleteInicio());
		assertFalse(lista.estaVazia());

		assertSame(data3, lista.deleteInicio());
		assertTrue(lista.estaVazia());	
	}

	@Test
	public void testAdicionaFinalRemoveFinal() {
		lista.insereFinal(data1);
		lista.insereFinal(data2);
		lista.insereFinal(data3);
		assertFalse(lista.estaVazia());

		assertSame(data3, lista.deleteFinal());
		assertFalse(lista.estaVazia());

		assertSame(data2, lista.deleteFinal());
		assertFalse(lista.estaVazia());

		assertSame(data1, lista.deleteFinal());
		assertTrue(lista.estaVazia());	
	}

	@Test
	public void testRecupera() {
		lista.insereFinal(data1);
		lista.insereFinal(data2);
		lista.insereInicio(data3);
		assertEquals(data3, lista.recupera(0));
		assertEquals(data1, lista.recupera(1));
		assertEquals(data2, lista.recupera(2));

		lista.deleteFinal();
		assertEquals(data3, lista.recupera(0));
		assertEquals(data1, lista.recupera(1));
		
		lista.deleteInicio();
		assertEquals(data1, lista.recupera(0));		
	}
	
	@Test
	public void testIterador() {
		Iterador it = lista.iterador();
		assertFalse(it.temProximo());
		
		lista.insereFinal(data1);
		lista.insereFinal(data2);
		lista.insereInicio(data3);
		it = lista.iterador();
		assertTrue(it.temProximo());
		assertSame(data3, it.proximo());
		assertTrue(it.temProximo());
		assertSame(data1, it.proximo());
		assertTrue(it.temProximo());
		assertSame(data2, it.proximo());
		assertFalse(it.temProximo());
	}

}*/
