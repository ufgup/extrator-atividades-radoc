package br.ufg.ms.extrator.entities.ativ;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;


/**
 * 
 * Classe responsavel por representar o layout das informações que seram gravadas no arquivo de texto
 *
 */

public class Atividade {
	
	private Integer sequencialAtividade;
	private String descricaoAtividade;
	private Float qtdeHorasAtividade;
	private Date dtInicioAtividade;
	private Date dtFimAtividade;
	private String codGrupoPontuacao;
	private Float pontos;
	private Float difDatasDias;
	/*
	 * Arrays usados para setar pontuação de acordo com a resolução
	 */
	private String[] categoriasCalculoCargaHoraria = {
		"001001001000",
		"001002001000"
	};
	private String[] categoriasCalculoAno = {
			"002003009;000",
			"002003016;002",
			"002003018;001",
			"003001001000",
			"003001002000",
			"003001003000",
			"003002001000",
			"003002002000",
			"003002003000",
			"003002004000",
			"003002005000",
			"004002001000",
			"004004002000",
			"004004003000",
			"005002010000"
	};
	private String[] categoriasCalculo150 = {
			"004002005000",
			"004004001000",
			"004004004000",
			"004004005000",
			"004004006000"
	};
	private String[] categoriasCalculoMes = {
			"004003001000",
			"004003002000",
			"004003003000",
			"004003004000",
			"004003005000",
			"004003006000",
			"004003007000",
			"004003008000",
			"004003009000",
			"004003010000",
			"004003011000",
			"004003012000",
			"004003013000",
			"005003001000",
			"005003002000",
			"005003003000"
	};
	
	public Float getDifDatasDias() {
		return difDatasDias;
	}

	public void setDifDatasDias(Float difDatasDias) {
		this.difDatasDias = difDatasDias;
	}

	public Integer getSequencialAtividade() {
		return sequencialAtividade;
	}
	
	public void setSequencialAtividade(Integer sequencialAtividade) {
		this.sequencialAtividade = sequencialAtividade;
	}
	
	public String getDescricaoAtividade() {
		return descricaoAtividade;
	}
	
	public void setDescricaoAtividade(String descricaoAtividade) {
		this.descricaoAtividade = descricaoAtividade.trim();
	}
	
	public Float getQtdeHorasAtividade() {
		return qtdeHorasAtividade;
	}
	
	
	/**
	 * Como regra definada na descrição do requisitos caso
	 * as atividades do RADOC nao possuem CHA(Carga Horaria da Atividade)
	 * o valor da CHA deve ser calculado seguindo a seguinte regra:
	 * 
	 *  Numero de dias entre a data inicial e a data final, Multiplicado por 8.
	 *  Caso as datas de inicio e fim não forem distintas devem ser consideradas identicas,
	 *  nessa situação somente sera 1 * 8.
	 * 
	 * @param qtdeHorasAtividade
	 */
	
	public void setQtdeHorasAtividade(Float qtdeHorasAtividade) {
		float horas = 0;
		if(qtdeHorasAtividade > 0){
			this.qtdeHorasAtividade = qtdeHorasAtividade;
		}
		else{
			horas = this.difDatasDias * 8;
			if(horas <= 0){
				horas = 8;
			}			
			this.qtdeHorasAtividade = horas;
		}
	}
	
	public Date getDtInicioAtividade() {
		return dtInicioAtividade;
	}
	
	public void setDtInicioAtividade(Date dtInicioAtividade) {
		this.dtInicioAtividade = dtInicioAtividade;
		if((this.getDtInicioAtividade() != null) && (this.getDtFimAtividade() != null)){
			long dt = (this.getDtFimAtividade().getTime() - this.getDtInicioAtividade().getTime());     
			
            /**
             * dividindo por 86400000L, pois o retorno é em milisegundos
             */
			
			long dias = (dt / 86400000L);
			this.difDatasDias = (float) dias;
		}
	}
	
	public Date getDtFimAtividade() {
		return dtFimAtividade;
	}
	
	public void setDtFimAtividade(Date dtFimAtividade) {
		this.dtFimAtividade = dtFimAtividade;
		if((this.getDtInicioAtividade() != null) && (this.getDtFimAtividade() != null)){
			long dt = (this.getDtFimAtividade().getTime() - this.getDtInicioAtividade().getTime());     
			
            /**
             * dividindo por 86400000L, pois o retorno é em milisegundos
             */
			
			long dias = (dt / 86400000L);
			this.difDatasDias = (float) dias;
		}
	}
	
	
	/**
	 * Este metodo somente retorna uma copia do objeto instanciado,
	 * com os mesmos valores mas com referencias distintas, assim não
	 * possobilitando o acesso direto a referencia princial
	 * 
	 * @return Atividade
	 * 
	 */
	
	public Atividade buildClone() {
		Atividade newAtv = new Atividade(this.getSequencialAtividade());
		newAtv.setDescricaoAtividade(this.getDescricaoAtividade());
		newAtv.setDtInicioAtividade(getDtInicioAtividade());
		newAtv.setDtFimAtividade(this.getDtFimAtividade());
		newAtv.setQtdeHorasAtividade(this.getQtdeHorasAtividade());
		newAtv.setCodGrupoPontuacao(this.getCodGrupoPontuacao());
		newAtv.setPontos(this.getPontos());
		return newAtv;
	}
	
	public Atividade(Integer sequencialAtividade) {
		super();
		this.sequencialAtividade = sequencialAtividade;
		this.pontos = (float) 0;
	}
	
	
	/**
	 * Sobrescrita do metodo toString para iniciar a formatação de
	 * saida das informações das atividades. Somente e introduzido
	 * nessa etapa a sequencia e os "pipes" (|) para dividir as 
	 * informações fornecidas.
	 * 
	 */

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.00");
		return MessageFormat.format("%n{5} | {6} | {0} | {1} | {2} | {3,date} | {4,date}", sequencialAtividade,
				descricaoAtividade, qtdeHorasAtividade, dtInicioAtividade, dtFimAtividade, codGrupoPontuacao, df.format(pontos).toString().replace(',', '.'));
	}

	public String getCodGrupoPontuacao() {
		return codGrupoPontuacao;
	}

	public void setCodGrupoPontuacao(String codGrupoPontuacao) {
		if(codGrupoPontuacao == null){
			this.codGrupoPontuacao = "000000000000";
		}
		else{
			this.codGrupoPontuacao = codGrupoPontuacao;
		}
	}
	
	public Float getPontos() {
		return pontos;
	}

	public void setPontos(Float pontos) {
		if(pontos != null){
			this.pontos = pontos;
		}
		else{
			this.pontos = (float) 0;
		}
	}
	
	public void setarPontuacao(float pontuacao) {
		// TODO Auto-generated method stub
		if(Arrays.asList(categoriasCalculoCargaHoraria).contains(this.getCodGrupoPontuacao())){
			this.setPontos((this.getQtdeHorasAtividade()*pontuacao));
		}
		else if(Arrays.asList(categoriasCalculoMes).contains(this.getCodGrupoPontuacao())){
			/*
			 * Dias/30 = numero de meses para calculo da pontuacao
			 */
			if((this.getDifDatasDias() != null) && (this.getDifDatasDias()>0)){
				this.setPontos(pontuacao * (this.getDifDatasDias()/30));
			}
		}
		else if(Arrays.asList(categoriasCalculoAno).contains(this.getCodGrupoPontuacao())){
			/*
			 * Dias/365 = numero de anos para calculo da pontuacao
			 */
			if((this.getDifDatasDias() != null) && (this.getDifDatasDias()>0)){
				this.setPontos(pontuacao * (this.getDifDatasDias()/365));
			}
		}
		else if(Arrays.asList(categoriasCalculo150).contains(this.getCodGrupoPontuacao())){
			/*
			 * Se a carga horária é maior ou igual a 150, recebe pontuacao da resolucao, se não recebe proporcional 
			 */
			if((this.getQtdeHorasAtividade() != null) && (this.getQtdeHorasAtividade()>0)){
				if(this.getQtdeHorasAtividade()>=150){
					this.setPontos(pontuacao);
				}
				else{
					this.setPontos(pontuacao*(this.getQtdeHorasAtividade()/150));
				}
			}
			
		}
		else{
			/*
			 * Se não está em nenhuma das acimas, a pontuação é absoluta
			 */
			this.setPontos(pontuacao);
		}					
	}

	/**
	 * Enum criado para auxilio na 
	 * captura de dados de cada extrator
	 */
	
	public enum TagsDados {
		TITULO_TRABALHO("Título do trabalho:"),
		DESCRICAO_ATV("Descrição Complementar:"),
		DESCRICAO_ATV_ADM("Descrição:"),
		DESCRICAO_ATV_EXT("Descrição da atividade:"),
		TABELA("Tabela:"),
		PORTARIA("Portaria"),
		TITULO_PRODUTO("Título do produto:"),
		DATA("Data:"),
		TABELA_PRODUTO("Descrição do produto:"),
		TITULO_PROJETO("Título do Projeto:"),
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
