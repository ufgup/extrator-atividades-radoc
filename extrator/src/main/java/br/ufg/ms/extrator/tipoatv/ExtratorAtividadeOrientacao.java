package br.ufg.ms.extrator.tipoatv;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.entities.ativ.Atividade;

public class ExtratorAtividadeOrientacao implements ExtratorAtividadeI {

	
	String marcadorInicio = "Curso Disciplina CHA";
	boolean iniciadaExtracao = false;
	
	@Override
	public void extrairDadosAtividade(Atividade atvAtual, String line, int lineNumber) {
		
	}

	private boolean isIniciadaExtracao() {
		return iniciadaExtracao;
	}

	private void setIniciadaExtracao(boolean iniciadaExtracao) {
		this.iniciadaExtracao = iniciadaExtracao;
	}

}
