package tech.jaya.daniel.octoevents.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.JavalinEvent
import io.javalin.json.JavalinJackson
import org.h2.tools.Server
import org.koin.core.KoinProperties
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.getProperty
import org.koin.standalone.inject
import tech.jaya.daniel.octoevents.controller.ExceptionMapping
import java.text.SimpleDateFormat

/**
 * Configurador da inicialização do servidor Javalin.
 * <br/><br/>
 * Esta classe configura e disponibiliza o método setup() para retornar um objeto representando o servidor,
 * que deve ser iniciado por outras classes.
 *
 */
class JavalinConfig : KoinComponent {

	private val router: Router by inject()
	private val exceptionMapping: ExceptionMapping by inject()

	fun setup(): Javalin {

		StandAloneContext.startKoin(KoinConfig.modules, KoinProperties(useKoinPropertiesFile = true))

		val server = createServer()
		router.register(server)
		exceptionMapping.register(server)
		configureMapper()

		return server
	}

	private fun createServer(): Javalin {

		return Javalin.create()
			.enableCorsForAllOrigins()
			.contextPath(getProperty("context"))
			.port(getProperty("server_port"))
			.event(JavalinEvent.SERVER_STOPPING) {
				StandAloneContext.stopKoin()
			}
			.maxBodySizeForRequestCache(10240)
	}

	private fun configureMapper() {
		val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
		JavalinJackson.configure(
			jacksonObjectMapper()
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.setDateFormat(dateFormat)
				.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
		)
	}

}