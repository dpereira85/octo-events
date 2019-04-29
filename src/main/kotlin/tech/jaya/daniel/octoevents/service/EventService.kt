package tech.jaya.daniel.octoevents.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import io.javalin.BadRequestResponse
import io.javalin.NotFoundResponse
import org.slf4j.LoggerFactory
import tech.jaya.daniel.octoevents.config.MessageBundle
import tech.jaya.daniel.octoevents.controller.ExceptionMapping
import tech.jaya.daniel.octoevents.domain.Event
import tech.jaya.daniel.octoevents.domain.helper.EventDeserializer
import tech.jaya.daniel.octoevents.repository.EventRepository
import java.text.MessageFormat

/**
 * Classe de serviço responsável por validar inputs e atender às demandas dos Controllers de Event.
 *
 * @param eventRepository - Classe de persistência de Events
 * @param bundle - Classe que contém as mensagens da API no idioma do usuário.
 */
class EventService(private val eventRepository: EventRepository, private val bundle: MessageBundle) {

	private val LOG = LoggerFactory.getLogger(EventService::class.java)

	/**
	 * Valida o número informado e em caso positivo, retorna todos os Events que possuem o número da Issue informada.
	 *
	 * @param issueNumber Int representando o número da Issue.
	 * @return List<Event> 
	 */
	fun findByIssue(issueNumber: Int): List<Event> {

		if (issueNumber <= 0) {
			LOG.warn("Issue #${issueNumber} inválida. Procedimento abortado.")
			throw BadRequestResponse(bundle.get("EventService.findByIssue.invalidIssueNumber"))
		}

		val events = eventRepository.findByIssueNumber(issueNumber)

		if (events.isNullOrEmpty()) {
			LOG.warn("Issue #${issueNumber} não foi encontrada.")
			val errorMsg = MessageFormat.format(bundle.get("EventService.findByIssue.notFound"), issueNumber)
			throw NotFoundResponse(errorMsg)
		} else {
			LOG.info("Issue #${issueNumber} encontrada com ${events.size} eventos.")
			return events
		}
	}

	/**
	 * Valida se o payload informado representa um Event válido, com todos os parâmetros obrigatórios informados.
	 * Em caso positivo, o Event é persistido.
	 *
	 * @param payload - String contendo o POST da requisição em formato JSON.
	 */
	fun create(payload: String) {

		var event: Event

		try {
			event = objectMapper().readValue(payload, Event::class.java)
		} catch (ex: Exception) {
			LOG.warn("Erro ao processar o JSON. Procedimento abortado.")
			LOG.debug("Stacktrace: ", ex)
			throw BadRequestResponse(bundle.get("EventService.create.invalidJson"))
		}

		when {
			event.issueNumber <= 0 -> {
				LOG.warn("${event.issueNumber} não é um número válido para uma Issue. Procedimento abortado.")
				throw BadRequestResponse(bundle.get("EventService.create.invalidIssueNumber"))
			}
			event.user.isNullOrBlank() -> {
				LOG.warn("Nome de usuário não foi informado. Procedimento abortado.")
				throw BadRequestResponse(bundle.get("EventService.create.invalidUser"))
			}
			event.body.isNullOrBlank() -> {
				LOG.warn("Descrição da Issue não foi informada. Procedimento abortado.")
				throw BadRequestResponse(bundle.get("EventService.create.invalidBody"))
			}
		}

		eventRepository.save(event)
	}

	private fun objectMapper(): ObjectMapper {
		val module = SimpleModule()
		module.addDeserializer(Event::class.java, EventDeserializer(Event::class.java))
		val mapper = ObjectMapper()
		mapper.registerModule(module)
		return mapper
	}
}