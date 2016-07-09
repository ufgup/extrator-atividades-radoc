package br.ufg.ms.extrator;

import org.junit.Assert;
import org.junit.Test;

import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ArquivoInvalidoException;
import br.ufg.ms.extrator.exception.ErroExtracaoException;

public class ExtratorAtividadeTextoTest {

	@Test
	public void testExtrairAtividadesTexto() throws ArquivoInvalidoException, ErroExtracaoException {
		Radoc radoc = new Radoc("../extra/exemplos/Radoc-2011-Final.pdf");
		ExtratorTextoPDF ext = new ExtratorTextoPDF(radoc);
		ext.processarRadoc();
		
		ExtratorAtividadeTexto extAtividade = new ExtratorAtividadeTexto(radoc);
		System.out.println(extAtividade.extrairAtividadesTexto().size());
		//Assert.assertTrue(extAtividade.extrairAtividadesTexto().size() == 25);
	}

}
