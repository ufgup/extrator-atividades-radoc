package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TITULO_TRABALHO;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static java.lang.Float.parseFloat;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.omg.PortableInterceptor.ForwardRequestHelper;
import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;
import br.ufg.ms.extrator.common.DataUtil;

public class ExtratorAtividadeOrientacao implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	boolean iniciadaExtracao = false;
	private String naturezaAtividade = "005";
	private String tipoAtividade = "001";
	private String[][] tabelaCategorias  = {
			{"001", "ALUNO ORIENTADO EM TESE DE DOUTORADO DEFENDIDA E APROVADA", "20"},
			{"002", "ALUNO CO-ORIENTADO EM TESE DE DOUTORADO DEFENDIDA E APROVADA", "7"},
			{"003", "ALUNO ORIENTADO EM TESE DE DOUTORADO EM ANDAMENTO", "10"},
			{"004", "ALUNO CO-ORIENTADO EM TESE DE DOUTORADO EM ANDAMENTO", "4"},
			{"005", "ALUNO ORIENTADO EM DISSERTAÇÃO DE MESTRADO DEFENDIDA E APROVADA", "15"},
			{"006", "ALUNO CO-ORIENTADO EM DISSERTAÇÃO DE MESTRADO DEFENDIDA E APROVADA", "5"},
			{"007", "ALUNO ORIENTADO EM DISSERTAÇÃO DE MESTRADO EM ANDAMENTO", "8"},
			{"008", "ALUNO CO-ORIENTADO EM DISSERTAÇÃO DE MESTRADO EM ANDAMENTO", "3"},
			{"009", "ALUNO ORIENTADO EM MONOGRAFIA DE ESPECIALIZAÇÃO APROVADA (MÁXIMO DE 24 PONTOS)", "8"},
			{"010", "ALUNO ORIENTADO EM MONOGRAFIA DE ESPECIALIZAÇÃO EM ANDAMENTO (TOTAL MÁXIMO A SER CONSIDERADO NESTE ITEM SÃO 12 PONTOS)", "4"},
			{"011", "ALUNO ORIENTADO EM RESIDÊNCIA MÉDICA OU EM RESIDÊNCIA MULTIPROFISSIONAL EM SAÚDE", "5"},
			{"012", "ALUNO ORIENTADO EM ESTÁGIO SUPERVISIONADO", "3"},
			{"013", "ALUNO ORIENTADO EM PROJETO DE FINAL DE CURSO", "3"},
			{"014", "ALUNO DE OUTRA IFE ORIENTADO EM TESE DE DOUTORADO DEFENDIDA E APROVADA", "6"},
			{"015", "ALUNO DE OUTRA IFE CO-ORIENTADO EM TESE DE DOUTORADO DEFENDIDA E APROVADA", "3"},
			{"016", "ALUNO DE OUTRA IFE ORIENTADO EM TESE DE DOUTORADO EM ANDAMENTO", "3"},
			{"017", "ALUNO DE OUTRA IFE CO-ORIENTADO EM TESE DE DOUTORADO EM ANDAMENTO", "2"},
			{"018", "ALUNO DE OUTRA IFE ORIENTADO EM DISSERTAÇÃO DE MESTRADO DEFENDIDA E APROVADA", "4"},
			{"019", "ALUNO DE OUTRA IFE CO-ORIENTADO EM DISSERTAÇÃO DE MESTRADO DEFENDIDA E APROVADA", "2"},
			{"020", "ALUNO DE OUTRA IFE ORIENTADO EM DISSERTAÇÃO DE MESTRADO EM ANDAMENTO", "2"},
			{"021", "ALUNO DE OUTRA IFE CO-ORIENTADO EM DISSERTAÇÃO DE MESTRADO EM ANDAMENTO", "1"},
			{"022", "ALUNO ORIENTADO EM PROGRAMA DE INICIAÇÃO CIENTÍFICA(PIBIC/PIVIC/PROLICEN/PICME-OBMEP)", "6"},
			//Adicionada essa linha com mesma categoria, pois em alguns radocs falta a palavra PICME-OBMEP
			//A falta de padronização dos radocs dificulta muito criar um padrão para o codigo seguir
			{"022", "ALUNO ORIENTADO EM PROGRAMA DE INICIAÇÃO CIENTÍFICA (PIBIC / PIVIC /PROLICEN)", "6"}, 
			{"013", "ALUNO ORIENTADO EM PROGRAMA DE INICIAÇÃO CIENTÍFICA JÚNIOR", "5"},
			{"024", "ALUNO ORIENTADO EM PROGRAMA ESPECIAL DE TREINAMENTO (PET)", "5"},
			{"025", "ALUNO ORIENTADO COM BOLSA DE DTI, PIBIT, AT, JOVENS TALENTOS E SIMILARES", "5"},
			{"026", "ALUNO ORIENTADO COM BOLSA DE LICENCIATURA (PIBID E SIMILARES)", "5"},
			{"027", "ALUNO ORIENTADO COM BOLSA EXTENSÃO/CULTURA/ENSINO", "5"},
			{"028", "ALUNO ORIENTADO EM PROJETOS DE EXTENSÃO/CULTURA/ENSINO SEM BOLSA", "3"},
			{"029", "ALUNO ORIENTADO COM BOLSA PROCOM OU SIMILAR, VINCULADA A PROJETO DE PESQUISA/EXTENSÃO/ENSINO", "5"},
			{"030", "ALUNO ORIENTADO EM PROGRAMA DE MONITORIA", "3"},
			{"031", "ALUNO ORIENTADO EM ATIVIDADE NÃO CURRICULAR COM BOLSA", "2"},
			{"032", "ALUNO ORIENTADO EM ATIVIDADE NÃO CURRICULAR SEM BOLSA", "1"},
			{"033", "PESQUISADOR SUPERVISIONADO EM ESTÁGIO DE PÓS-DOUTORAMENTO (PRODOC, PNPD, DCR, ENTRE OUTROS)", "8"},
			{"034", "ALUNO ORIENTADO EM PRÁTICA COMO COMPONENTE CURRICULAR (PCC)", "1"}
		};
	private String categoria;
	private String subCategoria = "000";
	private Float pontuacao = (float) 0;
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			return;
		}
		if (!isIniciadaExtracao() &&
			ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			setIniciadaExtracao(true);
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			String splitDescricao = ctrl.line.substring((TITULO_TRABALHO.toString()).length());
			ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
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
			ctrl.line.startsWith(CHA.toString())) {
			
			// atingiu final da atividade
			String[] chaEDatas = ctrl.line.split("CHA: | Data início: | Data término: | Tipo Orientação: \\w+");
			ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			if(chaEDatas[1].isEmpty()){
				ctrl.atvAtual.setQtdeHorasAtividade(parseFloat("0.0"));
			}
			else{
				ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			}
			
			ctrl.atvAtual.setarPontuacao(this.pontuacao);
			
			String CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			ctrl.atvAtual.setCodGrupoPontuacao(CodGrupoPontuacao);
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
