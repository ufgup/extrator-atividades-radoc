package br.ufg.ms.extrator.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Classe de logs da aplicação
 *
 */
public class AppLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("log"); 
	
	public static Logger logger() {
		return LOGGER;
	}
	
	public static Logger createLogger(Class clazz) {
		return LoggerFactory.getLogger(clazz.getName());
	}

}
