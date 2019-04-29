package tech.jaya.daniel.octoevents.repository

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tech.jaya.daniel.octoevents.domain.Event
import javax.sql.DataSource

private object Events : LongIdTable() {

	val action: Column<String> = varchar("action", 20)
	val issueNumber: Column<Int> = integer("issue").index() // Indice para otimizar buscas de eventos por Issue
	val title: Column<String> = varchar("title", 100)
	val body: Column<String> = varchar("body", 255)
	val user: Column<String> = varchar("user", 50)
	val repositoryUrl: Column<String> = varchar("repository_url", 200)
	val createdAt: Column<DateTime> = datetime("created_at")
	val updatedAt: Column<DateTime> = datetime("updated_at")

	fun toDomain(row: ResultRow): Event {
		return Event(
			action = row[action],
			issueNumber = row[issueNumber],
			title = row[title],
			body = row[body],
			user = row[user],
			repositoryUrl = row[repositoryUrl],
			createdAt = row[createdAt].toDate(),
			updatedAt = row[updatedAt].toDate()
		)
	}
}

/**
 * Classe de persistência responsável por buscar e persistir Events.
 * <br/><br/>
 * Retorna 400 Bad Request se o JSON for inválido ou não tiver os elementos obrigatórios.
 *
 * @param dataSource - Datasource da aplicação injetado pelo Koin
 */
class EventRepository(private val dataSource: DataSource) {
	
	private val LOG = LoggerFactory.getLogger(EventRepository::class.java)

	init {
		
		LOG.debug("Criando as tabelas no banco de dados.")
		
		transaction(Database.connect(dataSource)) {
			SchemaUtils.create(Events)
		}
		
		LOG.debug("Tabelas criadas com sucesso.")
	}

	/**
	 * Retorna todos os Events que possuem o número da Issue informada.
	 *
	 * @param number Int representando o número da Issue.
	 * @return List<Event> 
	 */
	fun findByIssueNumber(number: Int): List<Event> {
		
		LOG.info("Procurando as Issues de número ${number}.")
		
		var events : List<Event>

		events = transaction(Database.connect(dataSource)) {
			Events.select { Events.issueNumber eq number }
				.orderBy(Events.createdAt)
				.map { row -> Events.toDomain(row) }
		}
		
		LOG.info("Encontrados ${events.size} evento(s).")
		
		return events
	}

	/**
	 * Persiste o Event informado.
	 *
	 * @param event
	 */
	fun save(event: Event) {
		
		LOG.info("Gravando novo evento.")

		transaction(Database.connect(dataSource)) {
			Events.insert { row ->
				row[action] = event.action
				row[issueNumber] = event.issueNumber
				row[title] = event.title
				row[body] = event.body
				row[user] = event.user
				row[repositoryUrl] = event.repositoryUrl
				row[createdAt] = DateTime(event.createdAt)
				row[updatedAt] = DateTime(event.updatedAt)
			}
		}
		
		LOG.info("Novo evento gravado com sucesso.")
	}
}