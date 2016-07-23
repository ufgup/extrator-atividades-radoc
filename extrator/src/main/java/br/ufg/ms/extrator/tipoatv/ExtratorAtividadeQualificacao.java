package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.DESCRICAO_ATV_ADM;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;


/**
 * 
 * Classe responsavel por encapsular a regra de extração das Atividades
 * de Qualificação
 *
 */

public class ExtratorAtividadeQualificacao implements ExtratorAtividadeI {
	
	@SuppressWarnings(value="unused")
	private static final Logger log = AppLogger.logger();
	
	private String naturezaAtividade = "005";
	private String tipoAtividade = "003";
	private String categoria;
	private String subCategoria = "000";
	
	private String[][] tabelaCategorias = {
			{"001", "Docente regularmente matriculado em curso de doutorado com relatórios de pós-graduação aprovados", "12"},
			{"002", "Estágio Pós-Doutoral ou Estágio Sênior", "12"},
			{"003", "Docente em licença para capacitação (Artigo 87, Lei N.8112)", "12"},
			{"004", "Curso de aperfeiçoamento realizado com carga horária superior a 40 horas", "3"},
			{"005", "Curso de aperfeiçoamento realizado com carga horária inferior a 40 horas", "1"},
			{"006", "Participação em Congressos, Seminários, Encontros, Jornadas etc.", "1"}
	};	
	
	boolean iniciadaExtracao = false;
	private Float pontuacao = (float) 0;
	
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
			//log.debug("	Linha {}: Iniciando leitura efetiva das atividades de Atividades de qualificação na proxima linha", ctrl.lineNumber);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(CHA.toString())) {
			String[] chaEDatas = ctrl.line.split("CHA: |Data de início: | Data de término:");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(DESCRICAO_ATV_ADM.toString())) {
			String splitDescricao = ctrl.line.substring((DESCRICAO_ATV_ADM.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TABELA.toString())) {
			String tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			String[] catPontos = null;
			catPontos = ctrl.buscaDadosporCategoria(this.tabelaCategorias, tabelaAtividade);
			this.categoria = catPontos[0];
			this.pontuacao = parseFloat(catPontos[1]);
			
			String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
		}
		
		if (ctrl.atvAtual.getDescricaoAtividade() !=null &&
			ctrl.atvAtual.getQtdeHorasAtividade() != null ) {
			// atingiu final da atividade
			ctrl.atvAtual.setarPontuacao(this.pontuacao);
			ctrl.salvarAtvAtual = true;
			setIniciadaExtracao(false);
		}
		
	}
	
	private boolean isIniciadaExtracao() {
		return iniciadaExtracao;
	}

	private void setIniciadaExtracao(boolean iniciadaExtracao) {
		this.iniciadaExtracao = iniciadaExtracao;
		this.categoria = "";
		this.subCategoria = "000";
		this.pontuacao = (float) 0;
	}
}
