package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.DATA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

public class ExtratorAtividadeEnsinoTextoPos implements ExtratorAtividadeI {
	
	@SuppressWarnings(value="unused")
	private static final Logger log = AppLogger.logger();
	
	private static final Integer quantMinColunasAtivEnsino = 10;
	
	String marcadorInicioAtvEnsino = "Nível Curso Disciplina CHA";
	/*
	 * Como definido em resolução o fator de pontos para atividade de ensino é 10/30 
	 */
	private Float fatorPontos = (float)  0.3333;
	private String naturezaAtividade = "001";
	private String tipoAtividade = "002";
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
		if(iniciadaAtividadeEnsino &&
				ctrl.line.startsWith(DATA.toString())){
			// final de pagina, contém o cabeçalho, mas não a atividade
			setIniciadaAtividadeEnsino(false);
			return;
		}
		if (iniciadaAtividadeEnsino) {
			String[] splitDescAux = ctrl.line.split("\\d{1} \\d{1}");
			String[] splitDesc = splitDescAux[0].split(" ");
			
			// quantidade de colunas. Previne problemas com quebras de linha.
			// ver Radoc2012_detalhado
			if (splitDesc.length > quantMinColunasAtivEnsino) { 
				int tamanho = splitDesc.length;
				if( Character.isDigit(splitDesc[splitDesc.length-1].charAt(0))){
					tamanho = tamanho - 1; 			
				}
				
				if ((tamanho > 20) || (tamanho < 3)){
					return; //ignorar sileciosamente: quebra comum de texto na tabela
				}
				
				String descricao = ctrl.line.substring(ctrl.line.indexOf(splitDesc[1]), ctrl.line.indexOf(" "+splitDesc[tamanho-2]+" "));
				ctrl.atvAtual.setDescricaoAtividade(descricao);
				ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(splitDesc[tamanho-2]));				
				String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
				ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
				ctrl.atvAtual.setarPontuacao(fatorPontos);
				ctrl.salvarAtvAtual = true;
			}
		}
	}

}
