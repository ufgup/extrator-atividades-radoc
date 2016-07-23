package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;


/**
 * 
 * Classe responsavel por encapsular a regra de extração das Atividades
 * Academicas Especiais
 *
 */
public class ExtratorAtividadeAcadEspec implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	boolean iniciadaExtracao = false;
	private String naturezaAtividade = "005";
	private String tipoAtividade = "002";
	private String[][] tabelaCategorias  = {
			{"001", "Membro de banca de concurso para docente efetivo", ""},
			{"001;001", "Membro de banca de concurso para docente efetivo - Na instituição", "4"},
			{"001;002", "Membro de banca de concurso para docente efetivo - Em outra instituição", "6"},
			{"002", "Membro de banca de concurso para docente substituto", "2"},
			{"003", "Membro de banca de defesa de dissertação de mestrado", ""},
			{"003;001", "Membro de banca de defesa de dissertação de mestrado - Na instituição", "4"},
			{"003;002", "Membro de banca de defesa de dissertação de mestrado - Em outra instituição", "6"},
			{"004", "Membro de banca de defesa de tese de doutorado", ""},
			{"004;001", "Membro de banca de defesa de tese de doutorado - Na instituição", "6"},
			{"004;002", "Membro de banca de defesa de tese de doutorado - Em outra instituição 8"},
			{"005", "Membro de banca de qualificação de mestrado", ""},
			{"005;001", "Membro de banca de qualificação de mestrado - Na instituição", "3"},
			{"005;002", "Membro de banca de qualificação de mestrado - Em outra instituição", "4"},
			{"006", "Membro de banca de qualificação de doutorado", ""},
			{"006;001", "Membro de banca de qualificação de doutorado - Na instituição", "5"},
			{"006;002", "Membro de banca de qualificação de doutorado - Em outra instituição", "6"},
			{"007", "Membro de banca de defesa de monografia, projeto final de curso e outros tipos de bancas", "2"},
			{"008", "Membro de corpo de júri", ""},
			{"008;001", "Membro de corpo de júri - Concursos internacionais", "8"},
			{"008;002", "Membro de corpo de júri - Concursos nacionais", "6"},
			{"009", "Cursos, palestras ou treinamento não curricular ministrados para docentes, funcionários ou alunos da UFG", "2"},
			{"010", "Coordenador de projeto institucional de intercâmbio internacional", "10"}
		};
	private String categoria;
	private String subCategoria;	
	private Float pontuacao = (float) 0;

	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TABELA.toString())) {
			return;
		}
		if (!isIniciadaExtracao() &&
			ctrl.line.startsWith(TABELA.toString())) {
			setIniciadaExtracao(true);
			
			String splitDescricao = ctrl.line.substring((TABELA.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			
			String tabelaAtividade = splitDescricao;
			
			String[] catPontos = null;
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
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));	
		}			
		
		if (ctrl.atvAtual.getDescricaoAtividade() !=null &&
			ctrl.atvAtual.getQtdeHorasAtividade() != null ) {
			// atingiu final da atividade
			String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
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
