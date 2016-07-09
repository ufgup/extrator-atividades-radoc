package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.CHA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.DESCRICAO_ATV;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.PORTARIA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

public class ExtratorAtividadeAdministrativa implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	String marcadorInicio = "Atividades de qualificação";
	boolean iniciadaExtracao = false;
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TABELA.toString())) {
			return;
		}
		if (!isIniciadaExtracao() &&
			(ctrl.line.startsWith(TABELA.toString()) ||
			ctrl.line.startsWith(CHA.toString()))) {
			setIniciadaExtracao(true);
			log.debug("	Linha {}: Iniciando leitura efetiva das atividades administrativas na proxima linha", ctrl.lineNumber);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(PORTARIA.toString())) {
			String[] chaEDatas = ctrl.line.split("Portaria [\\d\\/NA]+| CHA: |Data início: | Data término:");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[2]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[3]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[4]));
			
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(DESCRICAO_ATV.toString())) {
			String splitDescricao = ctrl.line.substring((DESCRICAO_ATV.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			
		}
		
		if (ctrl.atvAtual.getDescricaoAtividade() !=null &&
			ctrl.atvAtual.getQtdeHorasAtividade() != null ) {
			// atingiu final da atividade
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
		DESCRICAO_ATV("Descrição:"),
		TABELA("Tabela:"),
		PORTARIA("Portaria"),
		CHA("CHA:");
		
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
