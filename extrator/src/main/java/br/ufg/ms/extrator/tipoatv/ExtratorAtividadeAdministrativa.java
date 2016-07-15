package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.CHA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.DESCRICAO_ATV;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.PORTARIA;
import static br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa.TagsDados.TABELA;
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
 * Administrativas
 *
 */

public class ExtratorAtividadeAdministrativa implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	private String[][] tabelaCategorias = {
			{"001", "Coordenador de projeto institucional com financiamento ou de contratos e "
				  + "convênio com plano de trabalho aprovado", "5"},
			{"002", "Coordenador de curso de especialização, residência médica ou residência "
				  + "multiprofissional em saúde (total máximo a ser considerado neste item são 10 "
				  + "pontos)", "10"},
		    {"003", "Membro representante de classe da carreira docente no CONSUNI", "10"},
		    {"004", "Membro do Conselho de Curadores ou do Plenário do CEPEC ou de Conselho de Fundações", "10"},
		    {"005", "Atividades acadêmicas e administrativas designadas por portaria do Reitor, "
		    	  + "Pró-Reitor ou Diretor de Unidade Acadêmica com carga horária >=150 horas", "10"}
			};
	
	String naturezaAtividade 	= "004";
	String tipoAtividade 		= "002";
	String subCategoria 		= "000";
	String categoria;
	
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
				ctrl.line.startsWith(TABELA.toString())) {
			String tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			this.categoria = buscaCategoria(tabelaAtividade);			
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(DESCRICAO_ATV.toString())) {
			String splitDescricao = ctrl.line.substring((DESCRICAO_ATV.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			
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
