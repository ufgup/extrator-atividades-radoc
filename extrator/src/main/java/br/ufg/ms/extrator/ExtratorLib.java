package br.ufg.ms.extrator;

import static br.ufg.ms.extrator.common.AppLogger.logger;

import java.util.ArrayList;
import java.util.List;

import br.ufg.ms.extrator.entities.ativ.Atividade;
import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ArquivoInvalidoException;
import br.ufg.ms.extrator.exception.ErroExtracaoException;

public class ExtratorLib {
	
	public static List<Atividade> extrairAtividades(String caminhoRadoc) {
		List<Atividade> atividadesExtraidas = new ArrayList<>();
		try {
			Radoc radoc = new Radoc(caminhoRadoc);
			ExtratorTextoPDF extratorTexto = new ExtratorTextoPDF(radoc);
			extratorTexto.processarRadoc();
			ExtratorAtividadeTexto extratorAtividades = new ExtratorAtividadeTexto(radoc);
			atividadesExtraidas = extratorAtividades.extrairAtividadesTexto();
		} catch (ArquivoInvalidoException e) {
			logger().error("Erro ao extrair de {}. Arquivo invalido. Cancelando extracao");
		} catch (ErroExtracaoException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
