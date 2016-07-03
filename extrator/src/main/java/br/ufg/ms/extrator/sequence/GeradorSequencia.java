package br.ufg.ms.extrator.sequence;

import java.util.HashMap;
import java.util.Map;

public class GeradorSequencia {
	
	private static Map<String, Sequencia> sequencia = new HashMap<String, Sequencia>();
	
	public static synchronized Sequencia para(String identificadorRadoc) {
		if (!sequencia.containsKey(identificadorRadoc)) {
			sequencia.put(identificadorRadoc, new Sequencia());
		} 
		return sequencia.get(identificadorRadoc);
	}

}
