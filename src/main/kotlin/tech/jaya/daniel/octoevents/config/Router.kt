package tech.jaya.daniel.octoevents.config

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import tech.jaya.daniel.octoevents.controller.EventListController
import tech.jaya.daniel.octoevents.controller.EventWebhookController

/**
 * Configurador das rotas da API.
 * <br/><br/>
 * Esta classe configura os caminhos dos endpoints no servidor Javalin, associando os caminhos aos métodos dos
 * respectivos controllers.
 *
 * @param evLtController - Controller responsável por trazer a lista de eventos de uma issue.
 * @param evWhController - Controller responsável por receber e gravar os eventos enviados pelo GitHub.
 */
class Router(private val evLtController: EventListController, private val evWhController: EventWebhookController) {

	fun register(app: Javalin) {
		app.routes {
			path("/webhooks") {
				post(evWhController::create)
			}
			path("/issues/:number/events") {
				get(evLtController::get)
			}
		}
	}
}
