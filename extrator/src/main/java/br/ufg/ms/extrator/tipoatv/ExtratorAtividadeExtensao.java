package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.DESCRICAO_ATV_EXT;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

public class ExtratorAtividadeExtensao implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	boolean iniciadaExtracao = false;
	private String naturezaAtividade = "003";
	
	/**
	 * Propriedade fixa pelo fato do extrator ser uma categoria - Extensão III -1 Atividades de Coordenação de Pesquisa 
	 */
	private String tipoAtividade = "002";
	private String categoria;
	private String subCategoria = "";
	private String[][] tabelaCategorias = {
					{"001",     "Coordenador de projeto de extensão aprovado com comprovação de financiamento (exceto bolsas)", "10"},
					{"002",     "Coordenador de projeto ou programa de extensão/cultura cadastrado na PROEC (total máximo a ser considerado neste item são 15 pontos) ", "5"},
				    {"003",     "Coordenador de contratos e de convênios de cooperação institucional internacional", "5"},
				    {"004",     "Coordenador de contratos e de convênios de cooperação institucional nacional", "3"},
				    {"005",     "Participante de projeto de extensão/cultura cadastrado na PROEC (total, máximo a ser considerado neste item são 15 pontos)", "3"},
				    {"006",     "Curso de extensão ministrado com 20 ou mais horas (total máximo a ser considerado neste item são 15 pontos)", "5"},
				    {"007",     "Curso de extensão ministrado com menos de 20 horas (total máximo a ser considerado neste item são 10 pontos)", "2"},
					{"008",     "Palestrante, conferencista ou participante em mesa redonda em evento científico, cultural ou artístico", ""}, 
				    {"008;001", "Palestrante, conferencista ou participante em mesa redonda em evento científico, cultural ou artístico - Evento internacional", "5"},				    
				    {"008;002", "Palestrante, conferencista ou participante em mesa redonda em evento científico, cultural ou artístico - Evento nacional", "4"},
					{"008;003", "Palestrante, conferencista ou participante em mesa redonda em evento científico, cultural ou artístico - Evento regional ou local", "3"}, 
					{"009",     "Promoção ou produção de eventos artísticos e científicos locais", ""},
					{"009;001", "Promoção ou produção de eventos artísticos e científicos locais - Presidente", "4"},
					{"009;002", "Promoção ou produção de eventos artísticos e científicos locais - Comissão organizadora", "2"},
					{"010",     "Promoção ou produção de eventos artísticos e científicos regionais", ""},
					{"010;001", "Promoção ou produção de eventos artísticos e científicos regionais - Presidente", "6"},
					{"010;002", "Promoção ou produção de eventos artísticos e científicos regionais - Comissão organizadora", "3"}, 
					{"011",     "Promoção ou produção de eventos artísticos e científicos nacionais", ""},
					{"011;001", "Promoção ou produção de eventos artísticos e científicos nacionais - Presidente", "8"},
					{"011;002", "Promoção ou produção de eventos artísticos e científicos nacionais - Comissão organizadora", "4"},
					{"012",     "Promoção ou produção de eventos artísticos e científicos internacionais", ""},
					{"012;001", "Promoção ou produção de eventos artísticos e científicos internacionais - Presidente", "10"},
					{"012;002", "Promoção ou produção de eventos artísticos e científicos internacionais - Comissão organizadora", "5"}
					
					};
	
	private Float pontuacao = (float) 0;
	private String linhaAnterior = "";
	boolean proxLinha = false;
	String tabelaSalva = "";
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TABELA.toString())) {
			
			/*
			 * salvando linha anterior, pois há categorias que se quebram em 2 linhas
			 */
			if (!(ctrl.line.startsWith(DESCRICAO_ATV_EXT.toString())) &&
					!(ctrl.line.startsWith(CHA.toString()))){
				linhaAnterior = ctrl.line;
			}
			
			return;
		}
		if ((!isIniciadaExtracao() &&
			ctrl.line.startsWith(TABELA.toString())) || proxLinha  ) {
			String tabelaAtividade = "";
			if(!proxLinha){
				tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			}
			else{
				tabelaAtividade = ctrl.line;
			}
			String[] catPontos = null;
			setIniciadaExtracao(true);
			
			if((tabelaAtividade.trim().equals("")) || (tabelaAtividade == null)){
				tabelaSalva = linhaAnterior;
				proxLinha = true;
				return;
			}
			if(proxLinha){
				tabelaAtividade =  tabelaSalva + " " + ctrl.line;
				proxLinha = false;
				tabelaSalva = "";
			} 		
			
			catPontos = ctrl.buscaDadosporCategoria(this.tabelaCategorias, tabelaAtividade);
			this.categoria = catPontos[0];
			
			if ((this.categoria.length() > 3) && (this.categoria != "000")) {
				String array[] 	= new String[2];
				array 			= categoria.split(";");
				this.categoria 		= array[0];
				this.subCategoria 	= array[1];
			}
			else if ((this.categoria.length() == 3) && (this.categoria != "000")) {
				this.subCategoria 	= "000";
			}
			else{ 
				//Não encontrou categoria
				this.categoria = "000";
				this.subCategoria = "000";
			}
			
			this.pontuacao = parseFloat(catPontos[1]);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(CHA.toString())) {
			String[] chaEDatas = ctrl.line.split("CHA: | Data início: | Data término:");
			
			//passamos a data primeiro, pois se não houver CHA informada, calcula-se pelas datas
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(DESCRICAO_ATV_EXT.toString())) {
			String splitDescricao = ctrl.line.substring((DESCRICAO_ATV_EXT.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
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
		this.subCategoria = "";
		this.pontuacao = (float) 0;
	}

}
