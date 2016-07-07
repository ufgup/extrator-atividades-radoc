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

public class ExtratorAtividadeTexto {
	private static Radoc radoc;
	private static final Logger log = AppLogger.logger();
	
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
	
	// contador será passado como parametro para o construtor da classe Atividade;
	private Integer contador = 1;
	
	private ExtratorAtividadeEnsinoTexto extAEnsino = new ExtratorAtividadeEnsinoTexto();
	private ExtratorAtividadeOrientacao extAOrientacao = new ExtratorAtividadeOrientacao();
	private ExtratorAtividadeExtensao extAExtensao = new ExtratorAtividadeExtensao();
	private ExtratorAtividadeQualificacao  extQualificacao = new ExtratorAtividadeQualificacao();
	private ExtratorAtividadeAcadEspec extAAcadEspe = new ExtratorAtividadeAcadEspec();
	private ExtratorAtividadeAdministrativa extAAdmin = new ExtratorAtividadeAdministrativa();
	private ExtratorAtividadeProjetos extAProjeto = new ExtratorAtividadeProjetos();
	
	public ExtratorAtividadeTexto(Radoc newRadoc) {
		radoc = newRadoc;
	}

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
