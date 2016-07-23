
import static br.ufg.ms.extrator.common.AppLogger.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import br.ufg.ms.extrator.ExtratorLib;
import br.ufg.ms.extrator.entities.ativ.Atividade;

/**
 * Licensed under APACHE v2. Uses AGPL Libraries
 * 
 * Classe principal da aplicacao.
 * Inicia o processo de extracao por 
 * meio da chamadas como nos exemplos
 */
public class ExtrairAtividades {
	
	/** Metodo principal da aplicacao
	 * 
	 * @param args 
	 * Os argumentos aceitos são caminhos de arquivos, 
	 * tanto absolutos como relativos. 							<br>
	 * - Multiplos arquivos sao aceitos 						<br>
	 * - Somente PDFs passiveis de leitura sao usados 			<br>
	 * Exemplo de chamada:
	 *  <pre>
	 * java ExtrairAtividades arquivo.pdf
	 * java ExtrairAtividades ../arquivo.pdf
	 * java ExtrairAtividades /etc/folder/arquivo.pdf ../arquivo.pdf
	 * </pre>
	 */
	public static void main(String[] args) {
		logger().debug("Iniciando....");
		List<String> arquivos = filtrarValidos(resolverPathArquivos(args));
		if (arquivos.isEmpty()) {
			logger().info("Nenhum arquivo para prosseguir. Finalizando");
			return;
		} else {
			logger().info("Iniciando extracao para {} RADOC", arquivos.size());
			extrairAtividadesRadocs(arquivos);
		}
		logger().info("Concluido");
	}
	
	private static void extrairAtividadesRadocs(List<String> radocs) {
		for (String radoc: radocs) {
			List<Atividade> atividades = ExtratorLib.extrairAtividades(radoc);
			
			logger().debug("{} atividades extraidas do arquivo {}", atividades.size(), radoc);
			try {
				//Gravando em txt o resultado da extração
				File arquivo = new File("AtividadesExtraídas.txt");
				if(!arquivo.exists()){
					arquivo.createNewFile();
				}
				FileWriter arq = new FileWriter(arquivo);
			    PrintWriter gravarArq = new PrintWriter(arq);
			    
			    //strings auxiliares para retorno
			    String texto = atividades.toString();
			    String txt = texto.substring(3,texto.length()-1);
			    
			    gravarArq.printf(txt.replaceAll(",", "").replaceAll(" .00 ", " 0.00 "));
				arq.close();
				logger().debug("Arquivo .txt gravado com sucesso!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger().debug("Erro ao gerar arquivo .txt!");
			}
			
		}
	}

	/** 
	 * Resolve o caminho dos arquivos informados em linha de comando 
	 * como argumentos desta classe.										<br/>
	 * - Se o caminho for relativo, resolve para o caminho absoluto 			<br/>
	 * - Se o caminho for absoluto, deixa estar								
	 * @param arquivos Argumentos passados para a aplicacao
	 * @return Uma lista de strings, cada uma um caminho completo de arquivo
	 * passado como argumento para a aplicacao
	 */
	public static List<String> resolverPathArquivos(String[] arquivos ) {
		arquivos = arquivos != null ? arquivos : new String[]{};
		ArrayList<String> listaSaneada = new ArrayList<String>();
		for (String arquivo: arquivos) {
			Path path = Paths.get(arquivo)
								.toAbsolutePath()
								.normalize();
			
			listaSaneada.add(path.toString());
		}
		return listaSaneada;
	}
	
	/**
	 * Dada uma lista de nomes qualificados de arquivos,
	 * verifica se eles sao do tipo esperado e existem no disco
	 * @param pathArquivos
	 * @return
	 */
	private static List<String> filtrarValidos(List<String> pathArquivos) {
		ArrayList<String> listaFiltrada = new ArrayList<String>();
		if (pathArquivos == null || pathArquivos.isEmpty()) {
			logger().debug("Nenhum arquivo valido oferecido");
		} else {
			for (String pathArquivo: pathArquivos) {
				logger().debug("Validação inicial de {}", pathArquivo);
				if (isValid(pathArquivo)) {
					/* quiet */
					listaFiltrada.add(pathArquivo);
				} else {
					logger().error("	Arquivo invalido . Ignorando...", pathArquivo);
				}
			}
		}
		return listaFiltrada;
	}
	
	public static boolean isValid(String pathArquivo) {
		return isPdf(pathArquivo) && arquivoExiste(pathArquivo);
	}
	/** Determina se um arquivo e um PDF, baseado no caminho
	 * dele em disco. Pode ser expandido no futuro para checkar o 
	 * mime-type em vez da extensao, para aumentar a precisao
	 * @param pathArquivo caminho do arquivo
	 * @return boolean : o arquivo é um PDF?
	 */
	public static boolean isPdf(String pathArquivo) {
		return pathArquivo!=null && pathArquivo.endsWith(".pdf");
	}
	
	
	/** Determina se um arquivo existe e é passivel de leitura
	 * @param pathArquivo caminho do arquivo
	 * @return boolean: o arquivo existe em disco e é legivel (readable) ?
	 */
	public static boolean arquivoExiste(String pathArquivo) {
		File file = new File(pathArquivo);
		return file.exists() && file.canRead();
	}
	

}
