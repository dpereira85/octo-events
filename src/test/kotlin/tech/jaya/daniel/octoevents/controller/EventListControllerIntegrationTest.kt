package tech.jaya.daniel.octoevents.domain.helper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.util.HttpUtil
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import tech.jaya.daniel.octoevents.config.JavalinConfig
import tech.jaya.daniel.octoevents.controller.ErrorResponse
import tech.jaya.daniel.octoevents.controller.EventListResponse
import tech.jaya.daniel.octoevents.domain.Event
import java.io.File
import java.text.SimpleDateFormat
import org.junit.BeforeClass
import org.junit.AfterClass

class EventListControllerTest {

	private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	companion object {

		@JvmStatic
		private lateinit var app: Javalin

		@JvmStatic
		private lateinit var http: HttpUtil

		@BeforeClass
		@JvmStatic
		fun start() {
			app = JavalinConfig().setup().start()
			http = HttpUtil(app.port())

			val file = File("src/test/resources/min_payload.json")
			val fileContent = file.readText()
			val jsonFile: JsonNode = ObjectMapper().readTree(fileContent)

			http.createEvent(jsonFile)
		}

		@AfterClass
		@JvmStatic
		fun stop() {
			app.stop()
		}
	}

	@Test
	fun `get all events from Issue number`() {

		// Arrange
		val eventExpected = Event(
			"edited",
			1,
			"Spelling error in the README file",
			"It looks like you accidently spelled 'commit' with two 't's.",
			"Codertocat",
			"https://api.github.com/repos/Codertocat/Hello-World",
			formatter.parse("2018-05-30T20:18:32Z"),
			formatter.parse("2018-05-30T20:18:32Z")
		)

		// Act
		val response = http.get<EventListResponse>("/api/issues/1/events")

		// Assert
		assertEquals(response.status, HttpStatus.OK_200)
		assertNotNull(response.body.results)
		assertEquals(response.body.results?.size, 1)
		assertEquals(response.body.results?.first(), eventExpected)
	}

	@Test
	fun `get status code 404 from wrong Issue number`() {

		// Arrange

		// Act
		val response = http.get<ErrorResponse>("/api/issues/2/events")

		// Assert
		assertEquals(response.status, HttpStatus.NOT_FOUND_404)
	}
}