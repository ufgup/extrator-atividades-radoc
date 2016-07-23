package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.DESCRICAO_ATV_ADM;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.PORTARIA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

/**
 * 
 * Classe responsavel por encapsular a regra de extração das Atividades
 * Administrativas
 *
 */

public class ExtratorAtividadeAdministrativa implements ExtratorAtividadeI {
	
	@SuppressWarnings(value="unused")
	private static final Logger log = AppLogger.logger();
	
	private String[][] tabelaCategorias = {
			{"001", "Coordenador de projeto institucional com financiamento ou de contratos e convênio com plano de trabalho aprovado", "5"},
			{"002", "Coordenador de curso de especialização, residência médica ou residência multiprofissional em saúde", "10"},
		    {"003", "Membro representante de classe da carreira docente no CONSUNI", "10"},
		    {"004", "Membro do Conselho de Curadores ou do Plenário do CEPEC ou de Conselho de Fundações", "10"},
		    {"005", "Atividades acadêmicas e administrativas designadas por portaria do Reitor, Pró-Reitor ou Diretor de Unidade Acadêmica com carga horária >=150 horas", "10"},
		    {"005", "ATIVIDADES ACADÊMICAS E ADMINISTRATIVAS DESIGNADAS POR PORTARIA DO REITOR, PRÓ-REITOR OU DIRETOR DE UNIDADE ACADÊMICA COM CARGAHORÁRIA >=150 HORAS", "10"}
	};
	
	String naturezaAtividade 	= "004";
	String tipoAtividade 		= "002";
	String subCategoria 		= "000";
	String categoria;
	private Float pontuacao = (float) 0;
	
	String marcadorInicio = "Atividades de qualificação";
	boolean iniciadaExtracao = false;
	private String linhaAnterior = "";
	boolean proxLinha = false;
	String tabelaSalva = "";
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		/*
		 * salvando linha anterior, pois há categorias que se quebram em 2 linhas
		 */
		if (!(ctrl.line.startsWith(PORTARIA.toString())) &&
				!(ctrl.line.startsWith(TABELA.toString())) &&
				!(ctrl.line.startsWith(CHA.toString()))){
			linhaAnterior = ctrl.line;
		}
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TABELA.toString())) {
			return;
		}
		if (!isIniciadaExtracao() &&
			(ctrl.line.startsWith(TABELA.toString()) ||
			ctrl.line.startsWith(CHA.toString()))) {
			setIniciadaExtracao(true);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(PORTARIA.toString())) {
			String[] chaEDatas = ctrl.line.split("Portaria [\\d\\/NA]+| CHA: |Data início: | Data término:");
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[2]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[3]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[4]));
			
		}
		if ((isIniciadaExtracao() &&
				ctrl.line.startsWith(TABELA.toString())) ||
				proxLinha) {
			String tabelaAtividade = null;
			String[] catPontos = null;
			
			tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			
			if((tabelaAtividade.trim().equals("")) || (tabelaAtividade == null)){
				tabelaSalva = linhaAnterior;
				proxLinha = true;
				return;
			}
			if(proxLinha){
				tabelaAtividade =  tabelaSalva + ctrl.line;
				proxLinha = false;
				tabelaSalva = "";
			}
			catPontos = ctrl.buscaDadosporCategoria(this.tabelaCategorias, tabelaAtividade);
			this.categoria = catPontos[0];	
			
			this.pontuacao = parseFloat(catPontos[1]);
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(DESCRICAO_ATV_ADM.toString())) {
			String splitDescricao = ctrl.line.substring((DESCRICAO_ATV_ADM.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			
		}
		
		String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
		ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
		
		if (ctrl.atvAtual.getDescricaoAtividade() !=null &&
			ctrl.atvAtual.getQtdeHorasAtividade() != null ) {
			
			if(((ctrl.atvAtual.getPontos() == null) || (ctrl.atvAtual.getPontos() <= 0)) && (this.pontuacao > 0)){
				ctrl.atvAtual.setarPontuacao(this.pontuacao);
			}
			
			// atingiu final da atividade
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
