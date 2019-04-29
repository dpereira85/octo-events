package tech.jaya.daniel.octoevents.controller

import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.LoggerFactory
import tech.jaya.daniel.octoevents.config.MessageBundle
import tech.jaya.daniel.octoevents.domain.Event
import tech.jaya.daniel.octoevents.service.EventService
import tech.jaya.daniel.octoevents.utils.HttpStatusCodeFormatter

/**
 * O response enviado pelo endpoint EventListController quando a requisição é bem sucedida.
 * <br/><br/>
 * Esta classe serve para enviar ao usuário da API uma response formatada
 * contendo uma List<Event> dos resultados da sua requisição.
 *
 * @param status - Código HttpStatus. Retorna '200 OK' ou '404 Not Found'.
 * @param results - List<Event> de eventos que atendem à requisição, se existirem. 
 */
data class EventListResponse(
	val status: String,
	val results: List<Event>? = null
)

/**
 * Endpoint que provê uma lista de Event mediante o número de uma Issue.
 * <br/><br/>
 * Este controller trata de requisições do tipo GET, retornando uma List<Event> de todos os Events que possuírem
 * o mesmo número da Issue que foi informado na requisição via PathParam.
 * <br/><br/>
 * Retorna 404 Not Found se não existir nenhum Event para a Issue solicitada.
 *
 * @param eventService - Serviço que contém a lógica de negócio do tratamento de Events.
 */
class EventListController(private val eventService: EventService) : KoinComponent {

	private val LOG = LoggerFactory.getLogger(EventListController::class.java)
	private val bundle: MessageBundle by inject()

	fun get(ctx: Context) {

		LOG.info("Processando requisição do recurso [${ctx.url()}] solicitada pelo IP: [${ctx.ip()}]")

		val issueNumber = ctx.validatedPathParam("number")
			.check({ it.isNotBlank() })
			.asClass<Int>()
			.getOrThrow()

		val events: List<Event> = eventService.findByIssue(issueNumber)
		val status = HttpStatus.Code.OK
		val response = EventListResponse(HttpStatusCodeFormatter.message(status), events)
		
		ctx.json(response).status(status.getCode())

		LOG.info("Solicitação atendida: Recurso [${ctx.url()}] para o IP [${ctx.ip()}].")
	}

}