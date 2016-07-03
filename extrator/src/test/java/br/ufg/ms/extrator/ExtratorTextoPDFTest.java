package br.ufg.ms.extrator;

import org.junit.Assert;
import org.junit.Test;

import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ArquivoInvalidoException;
import br.ufg.ms.extrator.exception.ErroExtracaoException;
import junit.framework.TestCase;

public class ExtratorTextoPDFTest extends TestCase {

	@Test
	public void testProcessarRadoc() throws ArquivoInvalidoException, ErroExtracaoException {
		Radoc radoc = new Radoc("../extra/exemplos/Radoc-2011-Final.pdf");
		ExtratorTextoPDF ext = new ExtratorTextoPDF(radoc);
		Assert.assertTrue(ext.processarRadoc());
	}
	
	

}
