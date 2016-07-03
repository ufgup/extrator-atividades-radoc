package br.ufg.ms.extrator;

import static br.ufg.ms.extrator.common.AppLogger.createLogger;
import static java.lang.Float.parseFloat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import br.ufg.ms.extrator.entities.ativ.Atividade;
import br.ufg.ms.extrator.entities.radoc.Radoc;
import br.ufg.ms.extrator.exception.ErroExtracaoException;

public class ExtratorAtividadeTexto {
	private static Radoc radoc;
	private static final Logger log = createLogger(ExtratorAtividadeTexto.class);
	
	private String[] secoesRadoc = {
		"Atividades de ensino",
		"Atividades de orientação",
		"Atividades em projetos",
		"Atividades de qualificação",
		"Atividades acadêmicas especiais",
		"Atividades administrativas",
		"Produtos"
	};
	
	
	private ExtratorAtividadeEnsinoTexto extAEnsino = new ExtratorAtividadeEnsinoTexto();
	
	public ExtratorAtividadeTexto(Radoc radoc) {
		this.radoc = radoc;
	}

	public List<Atividade> extrairAtividadesTexto() throws ErroExtracaoException {
		LinkedList<Atividade> atividades = new LinkedList<>();
		
		BufferedReader bufRead = new BufferedReader(new StringReader(radoc.getConteudoTextual()));
		String line=null;
		int lineNumber = 1;
		int iSecao = -1;  // posicao da secao do RADOC atual
		boolean keepReading = true;
		Atividade atvAtual = new Atividade();
		try {
			while( keepReading && (line=bufRead.readLine()) != null )
			{
				if (line.equals(secoesRadoc[iSecao+1])) {
					iSecao++;
					log.debug("Linha {} : Iniciando secao {}", lineNumber, secoesRadoc[iSecao]);
					if (!atividades.contains(atvAtual)) {
						atividades.add(atvAtual.buildClone());
					}
					atvAtual = new Atividade();
				}
				switch (iSecao) {
				case -1:
					break;

				case 0:
					extAEnsino.extrairDadosAtividade(atvAtual, line, lineNumber);
					break;
					
				case 1:
					
					break;
					
				case 2:
					break;
				case 3:
					break;
					
				case 4:
					break;
					
				case 5:
					break;
					
				default:
					keepReading = false;
					break;
					
				} 
				lineNumber++;
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
