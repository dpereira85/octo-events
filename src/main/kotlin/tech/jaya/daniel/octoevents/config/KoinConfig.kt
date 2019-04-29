package tech.jaya.daniel.octoevents.config

import org.koin.dsl.module.module
import tech.jaya.daniel.octoevents.controller.EventListController
import tech.jaya.daniel.octoevents.controller.EventWebhookController
import tech.jaya.daniel.octoevents.controller.ExceptionMapping
import tech.jaya.daniel.octoevents.repository.EventRepository
import tech.jaya.daniel.octoevents.service.EventService

/**
 * Configurador da inicialização do framework Koin.
 * <br/><br/>
 * Esta classe configura os módulos do Koin, injetando suas dependências, e disponibiliza a constante modules
 * para ser utilizada em outras classes, como a
 * @see tech.jaya.daniel.octoevents.config.JavalinConfig
 *
 */
object KoinConfig {

	private val configModule = module {
		single { JavalinConfig() }
		single {
			H2DatabaseConfig(
				getProperty("jdbc.url"),
				getProperty("db.username"),
				getProperty("db.password")
			).getDataSource()
		}
		single { Router(get(), get()) }
		single { ExceptionMapping() }
	}

	private val msgBundleModule = module {
		single { MessageBundle() }
	}

	private val eventsModule = module {
		single { EventListController(get()) }
		single { EventWebhookController(get()) }
		single { EventService(get(), get()) }
		single { EventRepository(get()) }
	}

	internal val modules = listOf(configModule, msgBundleModule, eventsModule)
}