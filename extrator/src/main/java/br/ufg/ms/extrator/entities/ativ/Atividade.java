package br.ufg.ms.extrator.entities.ativ;

import java.text.MessageFormat;
import java.util.Calendar;
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
			if(this.getDtFimAtividade() != null){
				long dt = (this.getDtFimAtividade().getTime() - this.getDtInicioAtividade().getTime());     
				
	            /**
	             * dividindo por 86400000L, pois o retorno é em milisegundos
	             */
				
				long dias = (dt / 86400000L); 
				horas = dias * 8;
				if(horas <= 0){
					horas = 8;
				}
			}
			
			this.qtdeHorasAtividade = horas;
		}
	}
	
	public Date getDtInicioAtividade() {
		return dtInicioAtividade;
	}
	
	public void setDtInicioAtividade(Date dtInicioAtividade) {
		this.dtInicioAtividade = dtInicioAtividade;
	}
	
	public Date getDtFimAtividade() {
		return dtFimAtividade;
	}
	
	public void setDtFimAtividade(Date dtFimAtividade) {
		this.dtFimAtividade = dtFimAtividade;
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
		return newAtv;
	}
	
	public Atividade(Integer sequencialAtividade) {
		super();
		this.sequencialAtividade = sequencialAtividade;
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
		return MessageFormat.format("%n{0} | {1} | {2} | {3,date} | {4,date}", sequencialAtividade,
				descricaoAtividade, qtdeHorasAtividade, dtInicioAtividade, dtFimAtividade);
	}
}
