package tech.jaya.daniel.octoevents.config

import org.koin.standalone.KoinComponent
import java.util.Locale
import java.util.PropertyResourceBundle
import java.util.ResourceBundle
/**
 * Classe auxiliar para leitura de properties de localização.
 *
 * Esta classe localiza e carrega o arquivo de mensagens do sistema mais apropriado para o seu Locale.
 * (default em pt_BR).
 */
class MessageBundle : KoinComponent {
	
	private val messages : ResourceBundle
	
	init {
		messages = PropertyResourceBundle.getBundle("Messages") 
	}
	
	/**
	 * Retorna a mensagem devida para a chave informada.
	 *
	 * @param key Property que se deseja extrair o texto localizado.
	 */
	fun get(key : String) : String {
		return messages.getObject(key) as String
	}
	
}