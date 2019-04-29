package tech.jaya.daniel.octoevents.domain.helper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.util.HttpUtil
import org.eclipse.jetty.http.HttpStatus
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.BeforeClass
import org.junit.Test
import tech.jaya.daniel.octoevents.config.JavalinConfig
import tech.jaya.daniel.octoevents.controller.EventListResponse
import tech.jaya.daniel.octoevents.domain.Event
import java.io.File
import java.text.SimpleDateFormat

class EventWebhookControllerTest {

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

		}

		@AfterClass
		@JvmStatic
		fun stop() {
			app.stop()
		}
	}

	@Test
	fun `post complete payload from Github to successfully record an event`() {
		// Arrange
		val eventExpected = Event(
			"edited",
			2,
			"Spelling error in the README file",
			"It looks like you accidently spelled 'commit' with two 't's.",
			"Codertocat",
			"https://api.github.com/repos/Codertocat/Hello-World",
			formatter.parse("2018-05-30T20:18:32Z"),
			formatter.parse("2018-05-30T20:18:32Z")
		)
		
		val payloadFile = File("src/test/resources/complete_payload.json")
		val fileContent = payloadFile.readText()
		val jsonFile: JsonNode = ObjectMapper().readTree(fileContent)

		http.createEvent(jsonFile)

		// Act
		val response = http.get<EventListResponse>("/api/issues/2/events")

		// Assert
		assertEquals(response.status, HttpStatus.OK_200)
		assertNotNull(response.body.results)
		assertEquals(response.body.results?.size, 1)
		assertEquals(response.body.results?.first(), eventExpected)
	}
	
	@Test
	fun `return status code 400 when a Issue number is not informed`() {

		// Act
		val fileContent = File("src/test/resources/missing_issue_number_payload.json")
		val jsonFile: JsonNode = ObjectMapper().readTree(fileContent)

		// Act
		val response = http.createEvent(jsonFile)
		
		// Assert
		assertEquals(response.status, HttpStatus.BAD_REQUEST_400)
	}
	
	@Test
	fun `return status code 400 when Issue's User is not informed`() {

		// Act
		val fileContent = File("src/test/resources/missing_user_payload.json")
		val jsonFile: JsonNode = ObjectMapper().readTree(fileContent)

		// Act
		val response = http.createEvent(jsonFile)
		
		// Assert
		assertEquals(response.status, HttpStatus.BAD_REQUEST_400)
	}
	
	@Test
	fun `return status code 400 when Issue's body is not informed`() {

		// Act
		val fileContent = File("src/test/resources/missing_body_payload.json")
		val jsonFile: JsonNode = ObjectMapper().readTree(fileContent)

		// Act
		val response = http.createEvent(jsonFile)
		
		// Assert
		assertEquals(response.status, HttpStatus.BAD_REQUEST_400)
	}
}