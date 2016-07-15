package br.ufg.ms.extrator;

/**
 * 
 * Esta interface define o contrato para ser implementado
 * por cada classe que será reposavel por extrair tipos diferentes
 * de atividades do Radoc
 *
 */

public interface ExtratorAtividadeI {
	
	public void extrairDadosAtividade(br.ufg.ms.extrator.ExtratorAtividadeTexto.ControleIteracao ctrl) ;

}
