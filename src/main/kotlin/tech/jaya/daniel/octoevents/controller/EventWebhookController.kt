package tech.jaya.daniel.octoevents.controller

import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.LoggerFactory
import tech.jaya.daniel.octoevents.config.MessageBundle
import tech.jaya.daniel.octoevents.service.EventService
import tech.jaya.daniel.octoevents.utils.HttpStatusCodeFormatter

/**
 * O response enviado pelo endpoint EventListController quando a requisição é bem sucedida.
 * <br/><br/>
 * Esta classe serve para enviar ao usuário da API uma response formatada
 * contendo informações sobre o resultado da sua requisição.
 *
 * @param status - Código HttpStatus. Retorna '201 Created' ou '400 Bad Request'.
 * @param message - Mensagem com mais informações sobre o retorno da API. 
 */
data class EventWebhookResponse(
	val status: String,
	val message: String
)

/**
 * Endpoint que recebe um JSON representando uma IssueEvent e o persiste no banco de dados.
 * <br/><br/>
 * Este controller trata de requisições do tipo POST, validando e inserindo no banco de dados os Eventos enviados pelo GitHub.
 * <br/><br/>
 * Retorna 400 Bad Request se o JSON for inválido ou não tiver os elementos obrigatórios.
 *
 * @param eventService - Serviço que contém a lógica de negócio do tratamento de Events.
 */
class EventWebhookController(private val eventService: EventService) : KoinComponent {

	private val LOG = LoggerFactory.getLogger(EventWebhookController::class.java)
	private val bundle: MessageBundle by inject()

	fun create(ctx: Context) {

		LOG.info("Processando requisição do recurso [${ctx.url()}] solicitada pelo IP: [${ctx.ip()}]")
		
		val payload = ctx.body()
		eventService.create(payload)

		val status = HttpStatus.Code.CREATED
		val response = EventWebhookResponse(HttpStatusCodeFormatter.message(status), bundle.get("EventController.create.success"))

		ctx.json(response).status(status.getCode())

		LOG.info("Solicitação atendida: Recurso [${ctx.url()}] para o IP [${ctx.ip()}].")
	}
}