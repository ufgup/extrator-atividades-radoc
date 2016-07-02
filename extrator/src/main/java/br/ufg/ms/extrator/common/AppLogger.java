package br.ufg.ms.extrator.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("log"); 
	
	public static Logger logger() {
		return LOGGER;
	}

}
