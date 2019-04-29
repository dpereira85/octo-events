package tech.jaya.daniel.octoevents.domain.helper

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import tech.jaya.daniel.octoevents.domain.Event
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.time.ZoneId
import java.text.SimpleDateFormat

/**
 * Classe com métodos de apoio à deserialização do IssueEvent do Github.
 *
 * O IssueEvent do Github contém muitos campos indesejados, esta classe deserializa o payload completo
 * para o nosso objeto compacto.
 */
class EventDeserializer(valueClass: Class<*>?) : StdDeserializer<Event>(valueClass) {

	val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * Deserializa o IssueEvent do GithHub's para o formato da data class Event.
	 *
	 * @param jp JSON parser.
	 * @param ctxt Deserialization Context.
	 */
	@Throws(IOException::class, JsonProcessingException::class)
	override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Event? {

		val jsonNode = jp.codec.readTree<JsonNode>(jp)
		var event: Event? = null

		if (jsonNode.has("issue")) {
			event = Event(
				jsonNode.get("action").asText(),
				jsonNode.get("issue").get("number").asInt(),
				jsonNode.get("issue").get("title").asText(),
				jsonNode.get("issue").get("body").asText(),
				jsonNode.get("issue").get("user").get("login").asText(),
				jsonNode.get("issue").get("repository_url").asText(),
				parseDate(jsonNode.get("issue").get("created_at").asText()),
				parseDate(jsonNode.get("issue").get("updated_at").asText())
			)
		}

		return event
	}

	private fun parseDate(strDate: String): Date? {
		return formatter.parse(strDate)
	}
}