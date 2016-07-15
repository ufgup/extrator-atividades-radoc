package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static java.lang.Float.parseFloat;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

public class ExtratorAtividadeEnsinoTextoPos implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	String marcadorInicioAtvEnsino = "Nível Curso Disciplina CHA";
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
			log.debug("	Linha {}: Iniciando leitura efetiva das atividades de ensino Pós na proxima linha", ctrl.lineNumber);
			return;
		}
		if (iniciadaAtividadeEnsino) {
			String[] splitDescAux = ctrl.line.split("\\d{1} \\d{1}");
			String[] splitDesc = splitDescAux[0].split(" ");
			
			if (splitDesc.length > 20) {
				return; //ignorar sileciosamente: quebra comum de texto na tabela
			}
			String descricao = ctrl.line.substring(ctrl.line.indexOf(splitDesc[1]), ctrl.line.indexOf(splitDesc[splitDesc.length-2]));
			ctrl.atvAtual.setDescricaoAtividade(descricao);
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(splitDesc[splitDesc.length-2]));				
			
			String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
			log.debug("	Descricao AE: {}  CHA: {} CodGrupoPontuacao: {}", 
					ctrl.atvAtual.getDescricaoAtividade(), 
					ctrl.atvAtual.getQtdeHorasAtividade(),
					ctrl.atvAtual.getCodGrupoPontuacao());
			ctrl.salvarAtvAtual = true;
		}
	}

}
