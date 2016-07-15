package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.TABELA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeProjetos.TagsDados.CHA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeProjetos.TagsDados.TITULO_PROJETO;
import static java.lang.Float.parseFloat;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

/**
 * 
 *Classe esta na resolucao como sub topico de atividade de pesquisa e extenção
 *
 */

public class ExtratorAtividadeProjetos implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	String marcadorInicio = "Atividades administrativas";
	boolean iniciadaExtracao = false;
	
	private String[][]
			tabelaCategorias = {
					{"001",     "Coordenador de projeto de extensão aprovado com comprovação de "
						  +     "financiamento (exceto bolsas)", "10"},
					{"002",     "Coordenador de projeto ou programa de extensão/cultura cadastrado na "
						  +     "PROEC (total máximo a ser considerado neste item são 15 pontos) ", "5"},
				    {"003",     "Coordenador de contratos e de convênios de cooperação institucional internacional", "5"},
				    {"004",     "Coordenador de contratos e de convênios de cooperação institucional nacional", "3"},
				    {"005",     "Participante de projeto de extensão/cultura cadastrado na PROEC (total, "
				    	  +     "máximo a ser considerado neste item são 15 pontos)", "3"},
				    {"006",     "Curso de extensão ministrado com 20 ou mais horas (total máximo a ser "
				    	  +     "considerado neste item são 15 pontos)", "5"},
				    {"007",     "Curso de extensão ministrado com menos de 20 horas (total máximo a ser "
				    	  +     "considerado neste item são 10 pontos)", "2"},
					{"008",     "Palestrante, conferencista ou participante em mesa redonda em evento científico, "
						  +     "cultural ou artístico", ""}, 
				    {"008;001", "Evento internacional (total máximo a ser considerado neste item são 15 "
				    	  +     "pontos)", "5"},				    
				    {"008;002", "Evento nacional (total máximo a ser considerado neste item são 12 pontos)", "4"},
					{"008;003", "Evento regional ou local (total máximo a ser considerado neste item são 9 "
						  +     "pontos)", "3"},
					{"009",     "Promoção ou produção de eventos artísticos e científicos locais", ""},
					{"009;001", "Presidente", "4"},
					{"009;002", "Comissão organizadora", "2"},
					{"010",     "Promoção ou produção de eventos artísticos e científicos regionais", ""},
					{"010;001", "Presidente", "6"},
					{"010;002", "Comissão organizadora", "3"}, 
					{"011",     "Promoção ou produção de eventos artísticos e científicos nacionais", ""},
					{"011;001", "Presidente", "8"},
					{"011;002", "Comissão organizadora", "4"},
					{"012",     "Promoção ou produção de eventos artísticos e científicos internacionais", ""},
					{"012;001", "Presidente", "10"},
					{"012;002", "Comissão organizadora", "5"}
					
					};
			
	
	/**
	 * Propriedade fixa pelo fato do extrator ser uma categoria
	 */
	private String naturezaAtividade = "003";
	
	/**
	 * Propriedade fixa pelo fato do extrator ser uma categoria
	 */
	private String tipoAtividade = "002";
	private String categoria;
	private String subCategoria;
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TITULO_PROJETO.toString()) ) {
			return;
		}
		if (!isIniciadaExtracao() &&
			ctrl.line.startsWith(TITULO_PROJETO.toString())) {
			setIniciadaExtracao(true);
			log.debug("	Linha {}:  Iniciando leitura efetiva das atividades de Atividades de projetos", ctrl.lineNumber);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(CHA.toString())) {
			String[] chaEDatas = ctrl.line.split("CHA: |Data Início: | Data Término:");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TABELA.toString())) {
			String tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			this.categoria = buscaCategoria(tabelaAtividade);
			
			if (this.categoria.length() > 3) {
				String array[] 	= new String[2];
				array 			= categoria.split(";");
				categoria 		= array[0];
				subCategoria 	= array[1];
			}
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TITULO_PROJETO.toString())) {
			String splitDescricao[] = ctrl.line.split(TITULO_PROJETO.toString());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao[1]);
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
	
	enum TagsDados {
		DESCRICAO_ATV("Descrição:"),
		TABELA("Tabela:"),
		CHA("CHA:"),
		INICIO_SIMPLES("Tipo Situação Fun"),
		TITULO_PROJETO("Título do Projeto:");
		
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
