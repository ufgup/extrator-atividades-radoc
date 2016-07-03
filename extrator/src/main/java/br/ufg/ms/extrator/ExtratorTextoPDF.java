package br.ufg.ms.extrator;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import br.ufg.ms.extrator.common.AppLogger;
import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ArquivoInvalidoException;
import br.ufg.ms.extrator.exception.ErroExtracaoException;
import ch.qos.logback.core.encoder.Encoder;

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
	

	public boolean processarRadoc() throws ErroExtracaoException {
		try {
			PdfReader reader = new PdfReader(radoc.getRadocFile().getAbsolutePath());
			StringBuilder strBuilderConteudo = new StringBuilder();
			for (int i = 1; i < reader.getNumberOfPages(); i++) {
				strBuilderConteudo.append(PdfTextExtractor.getTextFromPage(reader, i));
			}
			
			byte[] strAsBytes = strBuilderConteudo.toString().getBytes();
			
				
			radoc.setConteudoTextual(new String(strAsBytes, "UTF-8"));
		} catch (IOException e) {
			throw new ErroExtracaoException("Erro de extracao: " + e.getMessage(), e);
			
		}
		log.debug(radoc.getConteudoTextual());
		log.debug("Extraidos {} caracteres de {}", radoc.getConteudoTextual().length(), radoc.getRadocFile());
		return true;
	}

}
