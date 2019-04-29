package tech.jaya.daniel.octoevents.utils

import org.eclipse.jetty.http.HttpStatus

object HttpStatusCodeFormatter {
	
	fun message(status: HttpStatus.Code): String {
		return "${status.getCode()} ${status.getMessage()}"
	}
}