package tech.jaya.daniel.octoevents.domain.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.junit.Assert.assertEquals
import org.junit.Test
import tech.jaya.daniel.octoevents.domain.Event
import java.io.File
import java.text.SimpleDateFormat

class EventDeserializerTest {

	private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private fun objectMapper(): ObjectMapper {
		val module = SimpleModule()
		module.addDeserializer(Event::class.java, EventDeserializer(Event::class.java))
		val mapper = ObjectMapper()
		mapper.registerModule(module)
		return mapper
	}

	@Test
	fun `test deserialization of the minimum payload when the payload is valid`() {

		// Arrange
		val file = File("src/test/resources/min_payload.json")
		val eventExpected = buildEvent(1)

		// Act
		val eventRetrieved = objectMapper().readValue(file, Event::class.java)

		// Assert
		assertEquals(eventExpected, eventRetrieved)
	}

	@Test
	fun `test deserialization of the complete payload when the payload is valid`() {

		// Arrange
		val file = File("src/test/resources/complete_payload.json")
		val eventExpected = buildEvent(2)

		// Act
		val eventRetrieved = objectMapper().readValue(file, Event::class.java)

		// Assert
		assertEquals(eventExpected, eventRetrieved)
	}

	private fun buildEvent(issueNumber: Int): Event {
		return Event(
			"edited",
			issueNumber,
			"Spelling error in the README file",
			"It looks like you accidently spelled 'commit' with two 't's.",
			"Codertocat",
			"https://api.github.com/repos/Codertocat/Hello-World",
			formatter.parse("2018-05-30T20:18:32Z"),
			formatter.parse("2018-05-30T20:18:32Z")
		)
	}
}