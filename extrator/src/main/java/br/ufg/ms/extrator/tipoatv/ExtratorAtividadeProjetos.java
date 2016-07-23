package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TITULO_PROJETO;
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
	
	boolean iniciadaExtracao = false;		
	
	private String[][] tabelaCategorias = {
			{"001", "Coordenador de projeto conjuntos de pesquisa e cooperação científica (tipo PRODOC, PROCAD, PNPD, entre outros) e de cursos MINTER e DINTER	aprovados por órgãos oficiais de fomento", "10"},
			{"002", "Coordenador de projeto de pesquisa aprovado com comprovação de financiamento", "10"},
			{"003", "Coordenador de projeto de pesquisa aprovado sem financiamento", "5"},
			//Duplicada, pois no radoc aparecem assim 
			{"003", "COORDENAÇÃO DE PROJETO DE PESQUISA APROVADO SEM FINANCIAMENTO", "5"}
	};
	/**
	 * Propriedade fixa pelo fato do extrator ser uma categoria
	 */
	private String naturezaAtividade = "003";
	
	/**
	 * Propriedade fixa pelo fato do extrator ser uma categoria - Extensão III -1 Atividades de Coordenação de Pesquisa 
	 */
	private String tipoAtividade = "001";
	private String categoria;
	private String subCategoria = "000";
	private Float pontuacao = (float) 0;
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TITULO_PROJETO.toString()) ) {
			return;
		}
		if (!isIniciadaExtracao() &&
			ctrl.line.startsWith(TITULO_PROJETO.toString())) {
			setIniciadaExtracao(true);
			//log.debug("	Linha {}:  Iniciando leitura efetiva das atividades de Atividades de projetos", ctrl.lineNumber);
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
			String[] catPontos = null;
			catPontos = ctrl.buscaDadosporCategoria(this.tabelaCategorias, tabelaAtividade);
			this.categoria = catPontos[0];
			
			this.pontuacao = parseFloat(catPontos[1]);			
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
	}	
}
