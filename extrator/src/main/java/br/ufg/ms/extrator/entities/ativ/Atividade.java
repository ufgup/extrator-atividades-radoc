package br.ufg.ms.extrator.entities.ativ;

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
		this.qtdeHorasAtividade = qtdeHorasAtividade;
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
		Atividade newAtv = new Atividade();
		newAtv.setSequencialAtividade(this.getSequencialAtividade());
		newAtv.setDescricaoAtividade(this.getDescricaoAtividade());
		newAtv.setDtInicioAtividade(getDtInicioAtividade());
		newAtv.setDtFimAtividade(this.getDtFimAtividade());
		newAtv.setQtdeHorasAtividade(this.getQtdeHorasAtividade());
		return newAtv;
	}

	@Override
	public String toString() {
		return "\nAtividade [seq=" + sequencialAtividade 
				+ ", qtdeHoras=" + qtdeHorasAtividade 
				+ ", dtIni=" + dtInicioAtividade
				+ ", dtFim=" + dtFimAtividade
				+ ", desc=" + descricaoAtividade
				+"]";
	}
	
	

}
