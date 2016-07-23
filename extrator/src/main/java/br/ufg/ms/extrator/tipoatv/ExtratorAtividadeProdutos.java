package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.DATA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.DESCRICAO_ATV_EXT;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TITULO_PRODUTO;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA_PRODUTO;
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

public class ExtratorAtividadeProdutos implements ExtratorAtividadeI {
	
	private static final Logger log = AppLogger.logger();
	
	boolean iniciadaExtracao = false;
	private String naturezaAtividade = "002";
	private String tipoAtividade = "003";
	private String[][] tabelaCategorias  = {
			{"001", "Desenvolvimento de programa de computador (software) com registro no INPI ou com ampla disponibilização em ambientes de software livre", "20"},
			{"002", "Desenvolvimento de software com divulgação em periódicos indexados e com corpo editorial ou em anais de congresso científico", "10"},
			{"003", "Desenvolvimento de software para uso institucional (total máximo a ser considerado neste item são 10 pontos)", "5"},
			{"004", "Desenvolvimento e registro no INPI de topografia de circuito integrado", "20"},
			{"005", "Desenvolvimento de produto, processo ou técnica com registro de patente no INPI ou modelo de utilidade", "20"},
			{"006", "Desenvolvimento e registro no INPI de desenho industrial", "20"},
			{"007", "Desenvolvimento e registro no INPI de processo de indicação geográfica", "20"},
			{"008", "Desenvolvimento e registro no INPI de marcas", "5"},
			{"009", "Participação em comitê editorial de periódicos especializados indexados e de editoras universitárias", "10"},
			{"010", "Parecer de consultoria ad hoc em comitês de avaliação de concursos e editais de publicação de livros de editoras com corpo editorial", "3"},
			{"011", "Parecer de consultoria ad hoc para periódicos especializados com corpo editorial ou para instituições de fomento à pesquisa ou para livros de editoras com corpo editorial", "3"},
			//Duplicado, para exemplo de radoc
			{"011", "PARECER DE CONSULTORIA AD HOC PARA PERIÓDICOS ESPECIALIZADOS COM CORPO EDITORIAL OU PARA INSTITUIÇÕES DE FOMENTO A PESQUISA", "3"},
			{"012", "Projeto, parecer ou relatório técnico realizado em consultoria ou assessoria oficializada por convite, convênio, contrato ou designação", ""},	
			{"012;001", "Parecer com anotação de responsabilidade técnica (ART) ou registro de responsabilidade técnica (RRT)", "10"},
			{"012;002", "Parecer sem anotação de responsabilidade técnica (ART) ou registro de responsabilidade técnica (RRT)", "5"}, 
			{"012;003", "Projeto ou Relatório Técnico com anotação de responsabilidade técnica (ART) ou registro de responsabilidade técnica (RRT)", "20"},
			{"012;004", "Projeto ou Relatório Técnico sem anotação de responsabilidade técnica (ART) ou registro de responsabilidade técnica (RRT)", "10"},
			{"013", "Anais, Manuais, catálogos, boletins, com ficha bibliográfica (organizador /redator)", "5"},
			{"014", "Produção e publicação de mapas, cartas ou similares", "10"},
			{"015", "Desenvolvimento de maquete", "5"},
			{"016", "Manutenção de obra artística", ""},
			{"016;001", "Manutenção de obra artística - Restauração de obra artística", "20"},
			{"016;002", "Manutenção de obra artística - Conservação de obra artística", "10"},
			{"017", "Curadoria de exposições", "5"},
			{"018", "Produção de cinema, vídeo, rádio, TV ou mídias digitais", "0"},
			{"018;001", "Produção de cinema, vídeo, rádio, TV ou mídias digitais - Editor", "20"},
			{"018;002", "Produção de cinema, vídeo, rádio, TV ou mídias digitais - Participante", "3"}
		};
	private String categoria;
	private String subCategoria;
	private Float pontuacao = (float) 0;
	private String linhaAnterior = "";
	boolean proxLinha = false;
	String tabelaSalva = "";
	
	@Override
	public void extrairDadosAtividade(ControleIteracao ctrl) {
		if (!isIniciadaExtracao() &&
			!ctrl.line.startsWith(TABELA_PRODUTO.toString())) {
			
			/*
			 * salvando linha anterior, pois há categorias que se quebram em 2 linhas
			 */
			if (!(ctrl.line.startsWith(TITULO_PRODUTO.toString())) &&
					!(ctrl.line.startsWith(DATA.toString()))){
				linhaAnterior = ctrl.line;
			}
			
			return;
		}		
		if ((!isIniciadaExtracao() &&
				ctrl.line.startsWith(TABELA_PRODUTO.toString())) || proxLinha ) {
			setIniciadaExtracao(true);
			String tabelaAtividade = "";
			if(!proxLinha){
				tabelaAtividade = ctrl.line.substring((TABELA_PRODUTO.toString()).length());
			}
			else{
				tabelaAtividade = ctrl.line;
			}
			String[] catPontos = null;
			
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
				ctrl.line.startsWith(TITULO_PRODUTO.toString())) {
			String splitDescricao = ctrl.line.substring((TITULO_PRODUTO.toString()).length());
			
			if((splitDescricao != "") && (!splitDescricao.isEmpty())){
				ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			}
			else{
				ctrl.atvAtual.setDescricaoAtividade("Título do produto fora do padrão");
			}
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(DATA.toString()) &&
			ctrl.line.contains("Ano")) {
			
			// atingiu final da atividade
			String[] chaEDatas = ctrl.line.split("Data: | Ano de publicação: | Página inicial: | Página final: | Número de páginas: \\w+");
			if( chaEDatas[1] != ""){
				ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[1]));
			}
			if( chaEDatas[1] != ""){
				ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[1]));
			}
			//setando 0 para pegar pela data
			ctrl.atvAtual.setQtdeHorasAtividade(parseFloat("0.0"));
			
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
