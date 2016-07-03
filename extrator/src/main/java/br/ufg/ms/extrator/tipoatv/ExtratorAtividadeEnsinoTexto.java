package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;

public class ExtratorAtividadeEnsinoTexto implements ExtratorAtividadeI {
	
	private static final Logger log = createLogger(ExtratorAtividadeEnsinoTexto.class);
	
	String marcadorInicioAtvEnsino = "Curso Disciplina CHA";
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
			log.debug("	Linha {}: Iniciando leitura efetiva das atividades de ensino na proxima linha", ctrl.lineNumber);
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
			log.debug("	Descricao AE: {}  CHA: {}", ctrl.atvAtual.getDescricaoAtividade(), ctrl.atvAtual.getQtdeHorasAtividade());
			ctrl.salvarAtvAtual = true;
		}
	}

}
