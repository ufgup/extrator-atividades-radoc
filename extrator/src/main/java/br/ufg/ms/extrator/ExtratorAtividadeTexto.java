package br.ufg.ms.extrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeExtensao;
import br.ufg.ms.extrator.tipoatv.ExtratorAtividadeOrientacao;
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
		"Atividades de ensino",
		"Atividades de orientação",
		"Atividades em projetos",
		"Atividades de extensão",
		"Atividades de qualificação",
		"Atividades acadêmicas especiais",
		"Atividades administrativas",
		"Produtos"
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
	private ExtratorAtividadeOrientacao extAOrientacao 		= new ExtratorAtividadeOrientacao();
	private ExtratorAtividadeExtensao extAExtensao 			= new ExtratorAtividadeExtensao();
	private ExtratorAtividadeQualificacao  extQualificacao 	= new ExtratorAtividadeQualificacao();
	private ExtratorAtividadeAcadEspec extAAcadEspe 		= new ExtratorAtividadeAcadEspec();
	private ExtratorAtividadeAdministrativa extAAdmin 		= new ExtratorAtividadeAdministrativa();
	private ExtratorAtividadeProjetos extAProjeto 			= new ExtratorAtividadeProjetos();
	
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
		LinkedList<Atividade> atividades = new LinkedList<>();
		ControleIteracao ctrl = new ControleIteracao();
		log.debug(radoc.getConteudoTextual());
		BufferedReader bufRead = new BufferedReader(new StringReader(radoc.getConteudoTextual()));
		
		try {
			while( ctrl.keepReading && (ctrl.line=bufRead.readLine()) != null )
			{
				if (ctrl.line.equals(secoesRadoc[ctrl.iSecao+1])) {
					ctrl.iSecao++;
					log.debug("Linha {} : Iniciando secao {}", ctrl.lineNumber, secoesRadoc[ctrl.iSecao]);
					ctrl.atvAtual = new Atividade(contador);
				}
				switch (ctrl.iSecao) {
				case -1:
					break;
				case 0:
					extAEnsino.extrairDadosAtividade(ctrl);
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
		log.debug("Atividades: {}", atividades);
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
		
		@Override
		public String toString() {
			return "ControleIteracao [line=" + line + ", lineNumber=" + lineNumber + ", indxSecao=" + iSecao
					+ ", keepReading=" + keepReading + ", salvarAtvAtual=" + salvarAtvAtual + "]";
		}
		
		
	}

}
