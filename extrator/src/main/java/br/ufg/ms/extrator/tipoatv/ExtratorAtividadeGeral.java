package br.ufg.ms.extrator.tipoatv;

import static br.ufg.ms.extrator.common.DataUtil.toDate;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.CHA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TABELA;
import static br.ufg.ms.extrator.entities.ativ.Atividade.TagsDados.TITULO_TRABALHO;
import static java.lang.Float.parseFloat;

import org.slf4j.Logger;

import br.ufg.ms.extrator.ExtratorAtividadeI;
import br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao;
import br.ufg.ms.extrator.common.AppLogger;

public class ExtratorAtividadeGeral implements ExtratorAtividadeI {
	
	@SuppressWarnings(value="unused")
	private static final Logger log = AppLogger.logger();
	
	boolean iniciadaExtracao = false;
	private String naturezaAtividade;
	private String tipoAtividade;
	private String[][] tabelaCategorias  = {
			//II -1 Produção Científica 
			{"002;001;001;000", "Artigo completo ou texto literário publicado em periódico", ""},
			{"002;001;001;001", "Com classificação no Qualis/CAPES", "20"},
			{"002;001;001;002", "Não sujeito à classificação no Qualis/CAPES", "10"},
			{"002;001;002;000", "Resumo de artigo em periódicos especializados nacional ou internacional com corpo editorial", "5"},
			{"002;001;003;000", "Artigos ou textos literários em repositórios de publicação eletrônica ligados a editoras ou universidades (total máximo a ser considerado neste item são 10 pontos)", "5"},
			{"002;001;004;000", "Resumo expandido publicado em anais (total máximo a ser considerado neste item são 10 pontos) de congresso", ""},
			{"002;001;004;001", "Internacional", "8"},
			{"002;001;004;002", "Nacional", "6"},
			{"002;001;004;003", "Regional ou Local", "4"},
			{"002;001;005;000", "Resumo simples publicado em anais (total máximo a ser considerado neste item são 10 pontos) de congresso", ""},
			{"002;001;005;001", "Internacional", "4"},
			{"002;001;005;002", "Nacional", "3"},
			{"002;001;005;003", "Regional ou Local", "2"},
			{"002;001;006;000", "Trabalho completo publicado em anais de congresso cientifico", "10"},
			{"002;001;007;000", "Editor ou Coordenador editorial de livro publicado com selo de editora que possua corpo editorial", "20"},
			{"002;001;008;000", "Livro publicado com selo de editora que possua corpo editorial", "40"},
			{"002;001;009;000", "Livro publicado com selo de editora que não possua corpo editorial", "10"},
			{"002;001;010;000", "Capítulo de livro publicado com selo de editora que possua corpo editorial (total máximo a ser considerado neste item são 40 pontos)", "10"},
			{"002;001;011;000", "Edição ou organização de livro (coletânea) publicado com selo de editora que possua corpo editorial", "12"},
			{"002;001;012;000", "Capítulo traduzido de livro publicado com selo de editora que possua corpo editorial (total máximo a ser considerado neste item são 20 pontos)", "5"},
			{"002;001;013;000", "Tradução ou revisão científica de livro traduzido e publicado com selo de editora que possua corpo editorial", "10"},
			{"002;001;014;000", "Resenhas, prefácios ou verbetes", "10"},
			{"002;001;015;000", "Livro didático desenvolvido para projetos institucionais/governamentais", "10"},
			{"002;001;016;000", "Editor de periódicos especializados indexados com corpo editorial", "20"},
			{"002;001;017;000", "Editor de jornais ou revistas com distribuição", ""},
			{"002;001;017;001", "Local ou regional", "15"},
			{"002;001;017;002", "Nacional", "20"},
			{"002;001;017;003", "Internacional", "20"},
			{"002;001;018;000", "Dissertação de Mestrado defendida de aprovada (sendo o docente o autor da dissertação)", "10"},
			{"002;001;019;000", "Tese de Doutorado defendida e aprovada (sendo o docente o autor da tese)", "15"},
			//II - 2 Produção Artística e Cultural 
			{"002;002;001;000", "Criação, produção e direção de filmes, vídeos, discos, audiovisuais, coreografias, peças teatrais, óperas ou musicais, ou musicais apresentados em eventos", ""}, 	
			{"002;002;001;001", "Locais ou regionais", "10"},
			{"002;002;001;002", "Nacionais", "15"},
			{"002;002;001;003", "Internacionais", "20"},
			{"002;002;002;000", "Criação e produção do projeto gráfico de livros: concepção gráfica (mancha gráfica, diagramação, escolha de fonte)", "10"},
			{"002;002;003;000", "Design (gráfico, de luz, de figurino e formas animadas, cenográfico e similares)", "5"},
			{"002;002;004;000", "Design de impressos por peça (limitados a 20 pontos)", "1"},
			{"002;002;005;000", "Design de interfaces digitais", "10"},
			{"002;002;006;000", "Design de interfaces digitais com inovação tecnológica", "20"},
			{"002;002;007;000", "Produtos com inovação tecnológica", "20"},
			{"002;002;008;000", "Exposições e apresentações artísticas locais ou regionais", ""},
			{"002;002;008;001", "Participação individual, camerista, solista ou ator principal", "16"},
			{"002;002;008;002", "Participação coletiva ou coadjuvante", "5"},
			{"002;002;009;000", "Exposições e apresentações artísticas nacionais", ""},
			{"002;002;009;001", "Participação individual, camerista, solista ou ator principal", "20"},
			{"002;002;009;002", "Participação coletiva ou coadjuvante", "10"},
			{"002;002;010;000", "Exposições e apresentações artísticas internacionais", ""},
			{"002;002;010;001", "Participação individual, camerista, solista ou ator principal", "20"},
			{"002;002;010;002", "Participação coletiva ou coadjuvante", "15"},
			{"002;002;011;000", "Composições musicais", ""},
			{"002;002;011;001", "Editadas", "20"},
			{"002;002;011;002", "Publicadas em revistas científicas", "20"},
			{"002;002;011;003", "Gravadas", "15"},
			{"002;002;011;004", "Executadas em apresentações públicas", "15"},
			{"002;002;012;000", "Produção artística, arquitetônica ou de design premiada em evento", ""},
			{"002;002;012;001", "Local ou regional", "5"},
			{"002;002;012;002", "Nacional", "10"},
			{"002;002;012;003", "Internacional", "15"},
			{"002;002;013;000", "Arranjos musicais (canto, coral e orquestral)", "5"},
			{"002;002;014;000", "Apresentação artística ou cultural em rádio ou TV", "5"},
			{"002;002;015;000", "Sonoplastia (cinema, música, rádio, televisão, teatro)", "3"},
			//II - 4 Outro Tipo de Produção
			{"002;004;001;000", "Artigos de opinião veiculados em jornais e revistas (eletrônico ou impresso)", "1"},
			{"002;004;002;000", "Texto ou material didático para uso institucional (não fracionados e com ampla divulgação)", "2"},
			{"002;004;003;000", "Artigos de divulgação científica, tecnológica e artística veiculados em jornais e revistas (eletrônico ou impresso)", "3"},
			{"002;004;004;000", "Apresentação oral de trabalho publicado em anais de congresso científico (total máximo a ser considerado neste item são 9 pontos)", "3"},
			{"002;004;005;000", "Apresentação em painel de trabalho publicado em anais de congresso científico (total máximo a ser considerado neste item são 3 pontos)", " 1"},
			{"002;004;006;000", "Trabalho premiado em evento científico nacional ou internacional", "5"},
			{"002;004;007;000", "Tese, dissertação e trabalho de iniciação científica premiados por instituições de fomento (sendo o docente o autor ou orientador do produto)", "8"},
			//IV – 1 Direção e Função Gratificada 
			{"004;001;001;000", "Reitor ou Vice-Reitor ou Pró-Reitor", "14"},
			{"004;001;002;000", "Chefe de Gabinete", "10"},
			{"004;001;003;000", "Coordenador ou assessor vinculado à Reitoria", "10"},
			{"004;001;004;000", "Diretor de Unidade Acadêmica, de Unidade Acadêmica Especial ou do CEPAE", "10"},
			{"004;001;005;000", "Diretor de Campus do interior", "12"},
			{"004;001;006;000", "Vice-diretor de Campus do interior", "12"},
			{"004;001;007;000", "Diretor Geral do Hospital das Clínicas", "10"},
			{"004;001;008;000", "Coordenador ou assessor vinculado às Pró-Reitorias ou à Direção dos Campus do Interior", "8"},
			{"004;001;009;000", "Coordenador de Programa de Pós-Graduação stricto sensu", "8"},
			{"004;001;010;000", "Coordenador de Curso de Ensino Básico ou de Graduação", "8"},
			{"004;001;011;000", "Vice-diretor de Unidade Acadêmica ou Unidade Acadêmica Especial ou do CEPAE", "8"},
			{"004;001;012;000", "Diretor do Hospital Veterinário", "8"},
			{"004;001;013;000", "Diretor de Órgão da Administração (CERCOMP, CGA, CEGRAF, CIAR, DDRH, CS, SIASS, Museu, Rádio, Biblioteca etc.)", "8"},
			//IV – 3 Outras Atividades Administrativas
			{"004;003;001;000", "Presidente da CPPD", "7"},
			{"004;003;002;000", "Presidente da Comissão de Avaliação Institucional ou da Comissão Própria de Avaliação", "5"},
			{"004;003;003;000", "Membros da Coordenação Permanente do Centro de Seleção", "5"},
			{"004;003;004;000", "Diretores do HC", "5"},
			{"004;003;005;000", "Presidente do Comitê de Ética em Pesquisa da UFG e do HC/UFG", "5"},
			{"004;003;006;000", "Membros da CPPD ou da Comissão de Avaliação Institucional ou da Comissão Própria de Avaliação ou da CAD", "5"},
			{"004;003;007;000", "Coordenador de Pesquisa ou de Ensino ou de Extensão ou de Estágio das Unidades Acadêmicas", "3"},
			{"004;003;008;000", "Chefe de Departamento", "3"},
			{"004;003;009;000", "Chefe do Pronto Socorro ou da Maternidade ou do CEROF do Hospital das Clínicas da UFG", "3"},
			{"004;003;010;000", "Coordenador das Atividades de Interação com a Sociedade", "3"},
			{"004;003;011;000", "Coordenador das Atividades de Pesquisa e de Pós–Graduação lato sensu", "3"},
			{"004;003;012;000", "Membros do Comitê de Ética da UFG e do HC/UFG", "3"},
			{"004;003;013;000", "Membros do Comitê Interno e Externo do PIBIC", "3"},
			//IV – 4 Atividades de Representação Fora da UFG
			{"004;004;001;000", "Representante titular em conselho de classe profissional com carga horária igual ou superior a 150 horas", "10"},
			{"004;004;002;000", "Presidente do Sindicato de Docentes da UFG", "10"},
		    {"004;004;003;000", "Diretor do Sindicato de Docentes da UFG", "3"},
			{"004;004;004;000", "Representante sindical com carga horária igual ou superior a 150 horas", "10"},
			{"004;004;005;000", "Representante em entidade científica, artística e cultural com carga horária igual ou superior a 150 horas", "10"},
			{"004;004;006;000", "Representante em comissão de órgão governamental com carga horária igual ou superior a 150 horas", "10"}
		};
	private String categoria;
	private String subCategoria;
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
			//log.debug("	Linha {}: Iniciando leitura efetiva das atividades geral", ctrl.lineNumber);
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TITULO_TRABALHO.toString())) {
			String splitDescricao = ctrl.line.substring((TITULO_TRABALHO.toString()).length());
			if((splitDescricao != "") && (!splitDescricao.isEmpty())){
				ctrl.atvAtual.setDescricaoAtividade(splitDescricao);
			}
		}
		
		if (isIniciadaExtracao() &&
				ctrl.line.startsWith(TABELA.toString())) {
			String tabelaAtividade = ctrl.line.substring((TABELA.toString()).length());
			String[] catPontos = null;
			catPontos = ctrl.buscaDadosporCategoria(this.tabelaCategorias, tabelaAtividade);
			this.categoria = catPontos[0];
			
			if ((this.categoria.length() > 3) && (this.categoria != "000")) {
				String array[] 	= new String[4];
				array 			= categoria.split(";");
				this.naturezaAtividade = array[0];
				this.tipoAtividade     = array[1];
				this.categoria 		   = array[2];
				this.subCategoria 	   = array[3];
			}
			else{ 
				//Não encontrou categoria
				this.naturezaAtividade = "000";
				this.tipoAtividade     = "000";
				this.categoria = "000";
				this.subCategoria = "000";
			}
			
			this.pontuacao = parseFloat(catPontos[1]);
		}
		
		if (isIniciadaExtracao() &&
			ctrl.line.startsWith(CHA.toString())) {
			
			// atingiu final da atividade
			String[] chaEDatas = ctrl.line.split("CHA: | Data início: | Data término: \\w+");
			if( chaEDatas[2] != ""){
				ctrl.atvAtual.setDtInicioAtividade(toDate(chaEDatas[2]));
			}
			if( chaEDatas[3] != ""){
				ctrl.atvAtual.setDtFimAtividade(toDate(chaEDatas[3]));
			}
			if(chaEDatas[1].isEmpty()){
				ctrl.atvAtual.setQtdeHorasAtividade(parseFloat("0.0"));
			}
			else{
				ctrl.atvAtual.setQtdeHorasAtividade(parseFloat(chaEDatas[1]));
			}
			
			/**
			 * Caso a categoria não seja encontrada o codgrupoAtividade 
			 * deve ser zerado, para facilitar a busca e separação de informações 
			 * com inconsistencias na resolução e RADOC
			 */
			String CodGrupoPontuacao;
			
			if (categoria == "000") {
				CodGrupoPontuacao = "000000000000";
			}else {
				CodGrupoPontuacao = naturezaAtividade + tipoAtividade + categoria + subCategoria;
			}
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
