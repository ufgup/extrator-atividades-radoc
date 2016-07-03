package br.ufg.ms.extrator;

import org.slf4j.Logger;

import br.ufg.ms.extrator.common.AppLogger;
import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ArquivoInvalidoException;
import br.ufg.ms.extrator.exception.ErroExtracaoException;

public class ExtratorTextoPDF {
	
	private Radoc radoc;
	private static final Logger log = AppLogger.createLogger(ExtratorAtividadeTexto.class);
	private String textoBruto = "";
	
	public ExtratorTextoPDF(Radoc radoc) throws ArquivoInvalidoException, ErroExtracaoException {
		if (radoc == null) {
			throw new ArquivoInvalidoException();
		}
		setRadoc(radoc);
	}

	public Radoc getRadoc() {
		return radoc;
	}

	public void setRadoc(Radoc radoc) {
		this.radoc = radoc;
	}
	

	public void processarRadoc() throws ErroExtracaoException {
		// extracao
		log.debug("Extraidos {} caracteres de {}", radoc.getConteudoTextual().length(), radoc.getRadocFile());
	}

}
