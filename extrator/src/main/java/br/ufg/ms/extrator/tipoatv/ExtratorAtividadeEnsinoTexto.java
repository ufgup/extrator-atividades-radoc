package br.ufg.ms.extrator.tipoatv;

import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

public class ExtratorAtividadeEnsinoTexto implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	String marcadorInicioAtvEnsino = "Curso Disciplina CHA";
	/*
	 * Como definido em resolução o fator de pontos para atividade de ensino é 10/32 
	 */
	private Float fatorPontos = (float)  0.3125;
	private String naturezaAtividade = "001";
	private String tipoAtividade = "001";
	/*
	 * Conforme alinhado com professor Juliano, a categoria, para Atividade de Ensino, será sempre presencial = 001 
	 */
	private String categoria = "001";
	private String subCategoria = "000";

	boolean iniciadaAtividadeEnsino = false;
	
	public boolean isIniciadaAtividadeEnsino() {
		return iniciadaAtividadeEnsino;
	}

	public void setIniciadaAtividadeEnsino(boolean iniciadaAtividadeEnsino) {
		this.iniciadaAtividadeEnsino = iniciadaAtividadeEnsino;
	}
	
	
	public void extrairDadosAtividade(ControleIteracao ctrl) {		
		if (!isIniciadaAtividadeEnsino() && 
			!ctrl.line.startsWith(marcadorInicioAtvEnsino)) {
			return;
		}
		if (!isIniciadaAtividadeEnsino() && 
			ctrl.line.startsWith(marcadorInicioAtvEnsino)) {
			setIniciadaAtividadeEnsino(true);
			return;
		}
		if (iniciadaAtividadeEnsino) {
			String[] splitDesc = ctrl.line.split("\\d{1,2} \\d{4}");
			
			if (splitDesc.length < 2) {
				return; //ignorar sileciosamente: quebra comum de texto na tabela
			}
			ctrl.atvAtual.setDescricaoAtividade(splitDesc[0]);
			String cargaHorariaEAno = ctrl.line.substring(splitDesc[0].length(), ctrl.line.indexOf(splitDesc[1]));
			String splitCargaHorariaEAno[] = cargaHorariaEAno.split(" ");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(splitCargaHorariaEAno[0]));		
			String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
			ctrl.atvAtual.setarPontuacao(fatorPontos);
			ctrl.salvarAtvAtual = true;
		}
	}

}
