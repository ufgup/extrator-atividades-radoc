import static br.ufg.ms.extrator.common.AppLogger.logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
		logger().debug("Iniciando aplicacao");
		List<String> arquivos = filtrarValidos(resolverPathArquivos(args));
		if (arquivos.isEmpty()) {
			logger().info("Nenhum arquivo para prosseguir. Finalizando");
			return;
		} else {
			logger().info("Iniciando extracao para {} RADOC", arquivos.size());
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
				boolean valido = isPdf(pathArquivo) && arquivoExiste(pathArquivo);
				if (valido) {
					logger().info("Validacao com sucesso para {}", pathArquivo);
					listaFiltrada.add(pathArquivo);
				} else {
					logger().info("Validacao SEM sucesso para {}. Ignorando...", pathArquivo);
				}
			}
		}
		return listaFiltrada;
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
