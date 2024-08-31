package br.uefs.ecomp.bazar;

import br.uefs.ecomp.bazar.facade.TestesAceitacao;
import br.uefs.ecomp.bazar.model.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		UsuarioTest.class,
		ProdutoTest.class,
		LeilaoManualTest.class,
		ControllerBazarTest.class,
		TestesAceitacao.class,
		VendaTest.class,
		LeilaoAutomaticoTest.class,
		LeilaoAutomaticoFechadoTest.class
})
public class AllTests { }
