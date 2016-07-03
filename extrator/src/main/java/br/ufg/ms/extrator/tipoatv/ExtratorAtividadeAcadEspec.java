package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAcadEspec.TagsDados.CHA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAcadEspec.TagsDados.DESCRICAO_ATV;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAcadEspec.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;

public class ExtratorAtividadeAcadEspec implements ExtratorAtividadeI {
	
	private static final Logger log = createLogger(ExtratorAtividadeAcadEspec.class);
	
	String marcadorInicio = "Atividades acadêmicas especiais";
	boolean iniciadaExtracao = false;
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TABELA.toString())) {
			return;
		}
		if (!isIniciadaExtracao() &&
			ctrl.line.startsWith(TABELA.toString())) {
			setIniciadaExtracao(true);
			log.debug("	Linha {}: Iniciando leitura efetiva das Atividades acadêmicas especiais na proxima linha", ctrl.lineNumber);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(CHA.toString())) {
			String[] chaEDatas = ctrl.line.split("CHA: |Data de início: | Data de término:");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			
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
		DESCRICAO_ATV("Descrição Complementar:"),
		TABELA("Tabela:"),
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
