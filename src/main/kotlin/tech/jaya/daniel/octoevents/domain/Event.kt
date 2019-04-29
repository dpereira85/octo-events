package tech.jaya.daniel.octoevents.domain

import java.util.Date

/**
 * Informações resumidas sobre os Eventos recebidos do Github.
 */
data class Event(
	val action: String,
	val issueNumber: Int,
	val title: String,
	val body: String,
	val user: String,
	val repositoryUrl: String,
	val createdAt: Date?,
	val updatedAt: Date?
) {
}