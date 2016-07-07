package br.ufg.ms.extrator.entities.ativ;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

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
	
	public void setQtdeHorasAtividade(Float qtdeHorasAtividade) {
		float horas = 0;
		if(qtdeHorasAtividade > 0){
			this.qtdeHorasAtividade = qtdeHorasAtividade;
		}
		else{
			/* 
			   número de dias entre a data inicial
			   (“dtInicioAtividade”) e a data final (“dtFimAtividade”) 
			   da atividade multiplicado por oito 
			*/
			if(this.getDtFimAtividade() != null){
				long dt = (this.getDtFimAtividade().getTime() - this.getDtInicioAtividade().getTime());      
	            //dividindo por 86400000L, pois o retorno é em milisegundos
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
	
	

	@Override
	public String toString() {
		return MessageFormat.format("%n{0} | {1} | {2} | {3,date} | {4,date}", sequencialAtividade,
				descricaoAtividade, qtdeHorasAtividade, dtInicioAtividade, dtFimAtividade);
	}
	
	

}
