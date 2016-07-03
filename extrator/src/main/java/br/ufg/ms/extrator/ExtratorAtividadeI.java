package br.ufg.ms.extrator;

import br.ufg.ms.extrator.entities.ativ.Atividade;

public interface ExtratorAtividadeI {
	
	public void extrairDadosAtividade(Atividade atvAtual, String line, int lineNumber) ;

}
