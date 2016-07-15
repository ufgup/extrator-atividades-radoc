package br.ufg.ms.extrator.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
	
	private static DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	public static Date toDate(String str) {
		Date data = null;
		try {
            data = (java.util.Date)formatter.parse(str);
			return data;
		} catch (ParseException e) {
			AppLogger.logger().error("Erro ao fazer parse da data: {} :  {}", str.trim(), e.getMessage());
			return null;
		}
	}

}
