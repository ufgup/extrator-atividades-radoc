package br.ufg.ms.extrator.entities.ativ;

import br.ufg.ms.extrator.sequence.GeradorSequencia;

public class AtividadeBuilder {
	
	public Atividade criarAtividade(String idRadoc, String blocoTextoAtv) {
		Atividade atividade = new Atividade();
		Integer sequencial = GeradorSequencia.para(idRadoc).proximo();
		atividade.setSequencialAtividade(sequencial);
		return atividade;
	}

}
