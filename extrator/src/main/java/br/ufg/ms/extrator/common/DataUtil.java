package br.ufg.ms.extrator.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
	
	private static SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy 00:00:00"); 
	
	public static Date toDate(String str) {
		try {
			return fmt.parse(str.trim());
		} catch (ParseException e) {
			AppLogger.logger().error("Erro ao fazer parse da data: {} :  {}", str, e.getMessage());
			return null;
		}
	}

}
