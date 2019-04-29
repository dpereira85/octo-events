package tech.jaya.daniel.octoevents.controller

import io.javalin.BadRequestResponse
import io.javalin.Javalin
import io.javalin.NotFoundResponse
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.LoggerFactory
import tech.jaya.daniel.octoevents.config.MessageBundle
import tech.jaya.daniel.octoevents.utils.HttpStatusCodeFormatter
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.NullPointerException

/**
 * O response enviado pela API quando acontece um erro.
 * <br/><br/>
 * Esta classe serve para enviar ao usuário da API uma informação formatada sobre o erro que
 * aconteceu durante a sua requisição.
 *
 * @param status - Código HttpStatus representando o tipo de erro encontrado.
 * @param error - Nome do erro. Ex: NOT_FOUND.
 * @param exception - Nome da classe da exceção disparada.
 * @param message - Mensagem disparada pela exceção.
 */
internal data class ErrorResponse(val status : Int, val error: String, val exception: String, val message: String)


/**
 * Registro dos handlers de exceções do servidor.
 *
 * Esta classe configura a instância do servidor Javalin com os handlers apropriados para cada tipo de exceção
 * que espera encontrar durante o funcionamento da API.
 *
 */
class ExceptionMapping : KoinComponent {

	private val LOG = LoggerFactory.getLogger(ExceptionMapping::class.java)

	private val bundle: MessageBundle by inject()

	fun register(app: Javalin) {
		registerException(app, BadRequestResponse::class.java, HttpStatus.Code.BAD_REQUEST)
		registerException(app, NotFoundResponse::class.java, HttpStatus.Code.NOT_FOUND)
		registerException(app, NullPointerException::class.java, HttpStatus.Code.BAD_REQUEST)
		registerException(app, ExposedSQLException::class.java)
		registerException(app, Exception::class.java)
	}

	private fun registerException(
		app: Javalin,
		exClass: Class<out Exception>,
		status: HttpStatus.Code = HttpStatus.Code.INTERNAL_SERVER_ERROR
	) {

		val exClassName = exClass.canonicalName

		app.exception(exClass) { e, ctx ->

			LOG.warn("Exceção [${exClassName}] encontrada para a requisição [${ctx.url()}]. Mensagem : ${e.message}")

			val typeMsg = exClassName ?: "Undefined"
			val infoMsg = e.message ?: bundle.get("ExceptionMapping.${exClassName}")

			ctx.json(ErrorResponse(status.code, status.message, typeMsg, infoMsg))
				.status(status.getCode())
		}
	}

	private fun convertStacktraceToString(e: Exception): String {
		val sw = StringWriter()
		val pw = PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}