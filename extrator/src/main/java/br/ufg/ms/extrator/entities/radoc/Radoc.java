package br.ufg.ms.extrator.entities.radoc;

import java.io.File;

public class Radoc {

	private String path = "";
	private String conteudoTextual = "";
	private File radocFile;

	public Radoc(String path) {
		setPath(path);
		radocFile = new File(getPath());
	}

	private void setPath(String path) {
		this.path = path;
	}
	
	private String getPath() {
		return path;
	}

	public File getRadocFile() {
		return radocFile;
	}

	public void setRadocFile(File radocFile) {
		this.radocFile = radocFile;
	}

	public String getConteudoTextual() {
		return conteudoTextual;
	}

	public void setConteudoTextual(String conteudoTextual) {
		this.conteudoTextual = conteudoTextual;
	}

}
