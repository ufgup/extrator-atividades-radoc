package br.ufg.ms.extrator;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.entities.ativ.Atividade;

class ExtratorAtividadeEnsinoTexto {
	
	private static final Logger log = createLogger(ExtratorAtividadeEnsinoTexto.class);
	
	String marcadorInicioAtvEnsino = "Curso Disciplina CHA";
	boolean iniciadaAtividadeEnsino = false;
	
	public boolean isIniciadaAtividadeEnsino() {
		return iniciadaAtividadeEnsino;
	}

	public void setIniciadaAtividadeEnsino(boolean iniciadaAtividadeEnsino) {
		this.iniciadaAtividadeEnsino = iniciadaAtividadeEnsino;
	}
	
	
	protected void extrairDadoAtividadeEnsino(Atividade atvAtual, String line, int lineNumber) {
		if (!isIniciadaAtividadeEnsino() && 
			!line.startsWith(marcadorInicioAtvEnsino)) {
			return;
		}
		if (!isIniciadaAtividadeEnsino() && 
			line.startsWith(marcadorInicioAtvEnsino)) {
			setIniciadaAtividadeEnsino(true);
			log.debug("	Linha {}: Iniciando leitura efetiva das atividades de ensino na proxima linha", lineNumber);
			return;
		}
		if (iniciadaAtividadeEnsino) {
			String[] splitDescricao = line.split("\\d{1,2} \\d{4}");
			if (splitDescricao.length < 2) {
				return; //ignorar sileciosamente: quebra comum de texto na tabela
			}
			atvAtual.setDescricaoAtividade(splitDescricao[0]);
			String cargaHorariaEAno = line.substring(splitDescricao[0].length(), line.indexOf(splitDescricao[1]));
			String splitCargaHorariaEAno[] = cargaHorariaEAno.split(" ");
			atvAtual.setQtdeHorasAtividade(parseFloat(splitCargaHorariaEAno[0]));
			log.debug("	Descricao AE: {}  CHA: {}", atvAtual.getDescricaoAtividade(), atvAtual.getQtdeHorasAtividade());
		}
	}

}
