package br.ufg.ms.extrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import br.ufg.ms.extrator.common.AppLogger;
import br.ufg.ms.extrator.entities.ativ.Atividade;
import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ErroExtracaoException;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAcadEspec;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeAdministrativa;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeEnsinoTexto;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeEnsinoTextoPos;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeExtensao;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeGeral;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeOrientacao;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeProdutos;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeProjetos;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeQualificacao;


/**
 * 
 * Classe responsavel por pegar as informações da classe
 * Radoc que ja foram extraidas do arquivo Radoc e separar em atividades
 * delegando a cada classe responsavel de extração por tipo de atividade 
 * a encapsular a forma de extração e tratamento de dados
 *
 */

public class ExtratorAtividadeTexto {
	private static Radoc radoc;
	private static final Logger log = AppLogger.logger();
	
	/**
	 *Atributo que representa as sessoes de atividades do Radoc 
	 * 
	 */
	
	private String[] secoesRadoc = {
		"Atividades de ensino",						// 0
		"Atividades de orientação",					// 1
		"Atividades em projetos",					// 2
		"Atividades de extensão",					// 3
		"Atividades de qualificação",				// 4
		"Atividades acadêmicas especiais",			// 5
		"Atividades administrativas",				// 6
		"Produtos",									// 7
		"Produção Científica",						// 8
		"Produção Artística e Cultural", 			// 9
		"Produção Técnica e Tecnológica", 			// 10
		"Outro Tipo de Produção",					// 11
		"Outras Atividades Administrativas", 		// 12
		"Atividades de Representação Fora da UFG"	// 13
		
	};
	
	/**
	 * Este atributo contador representa o sequencial da ativdade,
	 * o controle dos valores do sequencial e realizado nesta classe
	 * sendo passado como parametro para cada nova atividade criada
	 */
	
	private Integer contador = 1;
	
	/**
	 * 
	 * Atributos que representam extratores especificos para cada tipo de atividade
	 * que são apresentadas no Radoc
	 * 
	 */
	
	private ExtratorAtividadeEnsinoTexto extAEnsino 		= new ExtratorAtividadeEnsinoTexto();
	private ExtratorAtividadeEnsinoTextoPos extAEnsinoPos 	= new ExtratorAtividadeEnsinoTextoPos();
	private ExtratorAtividadeOrientacao extAOrientacao 		= new ExtratorAtividadeOrientacao();
	private ExtratorAtividadeExtensao extAExtensao 			= new ExtratorAtividadeExtensao();
	private ExtratorAtividadeQualificacao  extQualificacao 	= new ExtratorAtividadeQualificacao();
	private ExtratorAtividadeAcadEspec extAAcadEspe 		= new ExtratorAtividadeAcadEspec();
	private ExtratorAtividadeAdministrativa extAAdmin 		= new ExtratorAtividadeAdministrativa();
	private ExtratorAtividadeProjetos extAProjeto 			= new ExtratorAtividadeProjetos();
	private ExtratorAtividadeProdutos extAProdutos  		= new ExtratorAtividadeProdutos();
	private ExtratorAtividadeGeral extAGeral				= new ExtratorAtividadeGeral();
	
	public ExtratorAtividadeTexto(Radoc newRadoc) {
		radoc = newRadoc;
	}

	
	/**
	 * Metodo resposanvel por extrair as atividades do Radoc.
	 * para cada tipo de atividade utiliza o extrator reponsavel
	 * 
	 * Interage ate encontrar as sessoes responsaveis por cada atividade
	 * fazendo um cruzamento de informações com a variavel secoesRadoc para
	 * determinar em qual sessao se encontra do documento PDF e utilizar o extrator
	 * adquado.
	 * 
	 * Cada interação do while, permite que um ou mais dados sejam extraidos da linha atual,
	 * para a atividade atual.
	 * 
	 * Cada extrator e capaz de marcar a atividade atual para ser salva quando interpreta
	 * o que esta pronto. 
	 * 
	 * Somente percorre uma unica vez cada linha extraida do Radoc, por eficiencia
	 * 
	 * @return List<Atividade>
	 * @throws ErroExtracaoException
	 */
	
	public List<Atividade> extrairAtividadesTexto() throws ErroExtracaoException {
//		log.debug(radoc.getConteudoTextual());    // conteudo bruto do radoc, recem extraido do pdf
		LinkedList<Atividade> atividades = new LinkedList<>();
		ControleIteracao ctrl = new ControleIteracao();
		BufferedReader bufRead = new BufferedReader(new StringReader(radoc.getConteudoTextual()));
		
		try {
			while( ctrl.keepReading && (ctrl.line=bufRead.readLine()) != null )
			{
				byte[] linhaAtual = ctrl.line.getBytes(Charset.forName("UTF-8"));
				byte[] proxSecao = secoesRadoc[ctrl.iSecao+1].getBytes(Charset.forName("UTF-8"));
				if (Arrays.equals(linhaAtual, proxSecao)) {
					ctrl.iSecao++;
					log.debug("Linha {} : Iniciando secao {}", ctrl.lineNumber, secoesRadoc[ctrl.iSecao]);
					ctrl.atvAtual = new Atividade(contador);
				}
				
				switch (ctrl.iSecao) {
				case -1:
					break;
				case 0:
					extAEnsino.extrairDadosAtividade(ctrl);
					extAEnsinoPos.extrairDadosAtividade(ctrl);
					break;
					
				case 1:
					extAOrientacao.extrairDadosAtividade(ctrl);
					break;
					
				case 2:
					extAProjeto.extrairDadosAtividade(ctrl);
					break;
				case 3:
					extAExtensao.extrairDadosAtividade(ctrl);
					break;
					
				case 4:
					extQualificacao.extrairDadosAtividade(ctrl);
					break;
					
				case 5:
					extAAcadEspe.extrairDadosAtividade(ctrl);
					break;
					
				case 6:
					extAAdmin.extrairDadosAtividade(ctrl);
					break;
				
				case 7:
					extAProdutos.extrairDadosAtividade(ctrl);
					break;
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
					extAGeral.extrairDadosAtividade(ctrl);
					break;
					
				default:
					ctrl.keepReading = false;
					break;
					
				} 
				if (ctrl.salvarAtvAtual) {
					contador++;
					atividades.add(ctrl.atvAtual.buildClone());
					ctrl.atvAtual = new Atividade(contador);
					ctrl.salvarAtvAtual = false;
				}
				ctrl.lineNumber++;
			}
		} catch (IOException e) {
			log.error("Linha com erro:\n" + ctrl.line);
			throw new ErroExtracaoException("Erro ao extrair linha " + ctrl.line + " do arquivo", e);
		}
		log.info("Extracao concluida com {} atividades", atividades.size());
		//log.debug("Atividades: {}", atividades);
		return atividades;
	}
	
	
	/**
	 * Classe responsavel por auxiliar na interação
	 * das informações extraidas do Radoc
	 *  
	 */
	public class ControleIteracao {
		public String line=null;
		public int lineNumber = 1;
		public int iSecao = -1;  // posicao da secao do RADOC atual
		public Boolean keepReading = true;
		public Atividade atvAtual;
		public Boolean salvarAtvAtual = false;
		
		public String[] buscaDadosporCategoria(String[][] tabelaCategorias, String tabelaAtividade) {
			String[] retorno = {
					"000", //categoria	
					"0" //pontos
			};
			if((tabelaAtividade.trim() != "") && (tabelaAtividade != null)){
				for(int i=0; i < tabelaCategorias.length; i++){
					byte[] linhaAtual = tabelaAtividade.toUpperCase().replaceAll(" ", "").trim().getBytes(Charset.forName("UTF-8"));
					byte[] catAtual = tabelaCategorias[i][1].toUpperCase().replaceAll(" ", "").trim().getBytes(Charset.forName("UTF-8"));
					
					if (Arrays.equals(linhaAtual, catAtual)) {
						retorno[0] = tabelaCategorias[i][0];
						retorno[1] = tabelaCategorias[i][2];
					}
				}	
			}
			return retorno;
		}
		
		@Override
		public String toString() {
			return "ControleIteracao [line=" + line + ", lineNumber=" + lineNumber + ", indxSecao=" + iSecao
					+ ", keepReading=" + keepReading + ", salvarAtvAtual=" + salvarAtvAtual + "]";
		}
		
		
	}

}
