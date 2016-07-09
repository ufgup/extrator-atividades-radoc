package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeOrientacao.TagsDados.CHA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeOrientacao.TagsDados.TITULO_TRABALHO;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;
import br.ufg.ms.extrator.common.DataUtil;

public class ExtratorAtividadeOrientacao implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	String marcadorInicio = "Atividades de orientação";
	boolean iniciadaExtracao = false;
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			return;
		}
		if (!isIniciadaExtracao() &&
			ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			setIniciadaExtracao(true);
			log.debug("	Linha {}: Iniciando leitura efetiva das atividades de orientacao", ctrl.lineNumber);
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			String splitDescricao = ctrl.line.substring((TITULO_TRABALHO.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(CHA.toString())) {
			// atingiu final da atividade
			String[] chaEDatas = ctrl.line.split("CHA: | Data início: | Data término: | Tipo Orientação: \\w+");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			ctrl.salvarAtvAtual = true;
		}
		
		
		
	}

	private boolean isIniciadaExtracao() {
		return iniciadaExtracao;
	}

	private void setIniciadaExtracao(boolean iniciadaExtracao) {
		this.iniciadaExtracao = iniciadaExtracao;
	}
	
	enum TagsDados {
		TITULO_TRABALHO("Título do trabalho:"),
		CHA("CHA");
		
		private String str;

		TagsDados(String str) {
			this.str = str;
		}
		
		@Override
		public String toString() {
			return str;
		}
	}

}
