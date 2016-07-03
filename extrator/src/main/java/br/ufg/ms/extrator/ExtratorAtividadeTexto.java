package br.ufg.ms.extrator;

import java.util.ArrayList;
import java.util.List;

import br.ufg.ms.extrator.entities.ativ.Atividade;
import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ErroExtracaoException;

public class ExtratorAtividadeTexto {
	
	private static Radoc radoc;
	
	public ExtratorAtividadeTexto(Radoc radoc) {
		this.radoc = radoc;
	}

	public List<Atividade> extrairAtividadesTexto() throws ErroExtracaoException {
		// manipular texto do RADOC e extrair as atividades
		return new ArrayList<>();
	}
	
	private Atividade extrairAtividadeTexto(String blocoAtividade) {
		return new Atividade();
	}

}
