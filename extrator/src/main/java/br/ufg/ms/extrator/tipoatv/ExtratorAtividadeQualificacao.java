package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.TABELA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeQualificacao.TagsDados.CHA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeQualificacao.TagsDados.DESCRICAO_ATV;
import static java.lang.Float.parseFloat;

import java.nio.charset.Charset;
import java.util.Arrays;

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
	
	private static final Logger log = AppLogger.logger();
	
	
	private String[][] tabelaCategorias = {
			{"001", "Docente regularmente matriculado em curso de doutorado com relatórios de"
				  + " pós-graduação aprovados (pontuação por mês de curso)", "12"},
			{"002", "Estágio Pós-Doutoral ou Estágio Sênior (pontuação por mês de estágio)", "12"},
			{"003", "Docente em licença para capacitação (Artigo 87, Lei N.8112) (pontuação por"
				  + " mês de licença)", "12"},
			{"004", "Curso de aperfeiçoamento realizado com carga horária superior a 40 horas", "3"},
			{"005", "Curso de aperfeiçoamento realizado com carga horária inferior a 40 horas", "1"},
			{"006", "Participação em Congressos, Seminários, Encontros, Jornadas etc. (total"
				  + " máximo a ser considerado neste item são 3 pontos)", "1"}
	};
	
	private String naturezaAtividade = "005";
	private String tipoAtividade = "003";
	private String categoria;
	private String subCategoria = "000";
	
	/**
	 * Essa variavel marcador inicio nao esta em uso mas pode confundir muita coisa
	 * entao cuidado ao alterar ou remover, essa classa esta extraindo os dados de qualificação msm
	 *
	 */
	String marcadorInicio = "Atividades administrativas";
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
			log.debug("	Linha {}: Iniciando leitura efetiva das atividades de Atividades de qualificação na proxima linha", ctrl.lineNumber);
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
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TABELA.toString())) {
			String tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			this.categoria = buscaCategoria(tabelaAtividade);
		}
		
		String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
		ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
		
		if (ctrl.atvAtual.getDescricaoAtividade() !=null &&
			ctrl.atvAtual.getQtdeHorasAtividade() != null ) {
			// atingiu final da atividade
			ctrl.salvarAtvAtual = true;
		}
		
	}
	
	private String buscaCategoria(String tabelaAtividade) {
		for(int i=0; i < tabelaCategorias.length; i++){
			byte[] linhaAtual = tabelaAtividade.toUpperCase().trim().getBytes(Charset.forName("UTF-8"));
			byte[] catAtual = tabelaCategorias[i][1].toUpperCase().trim().getBytes(Charset.forName("UTF-8"));
			
			if (Arrays.equals(linhaAtual, catAtual)) {
				return tabelaCategorias[i][0];
			}
		}
		return "000";		
	}
	
	private boolean isIniciadaExtracao() {
		return iniciadaExtracao;
	}

	private void setIniciadaExtracao(boolean iniciadaExtracao) {
		this.iniciadaExtracao = iniciadaExtracao;
	}
	
	/**
	 * Enum criado no mesmo arquivo pois ele 
	 * e somente usando para o contexto desta classe
	 *
	 */
	
	enum TagsDados {
		DESCRICAO_ATV("Descrição:"),
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
