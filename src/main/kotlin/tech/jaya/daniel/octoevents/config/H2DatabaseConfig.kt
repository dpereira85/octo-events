package tech.jaya.daniel.octoevents.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.tools.Server
import javax.sql.DataSource

/**
 * Configurador da inicialização da Database H2.
 * <br/><br/>
 * Esta classe configura e instancia o connection pool da database a partir de parâmetros obtidos pelo arquivo 'koin.properties'.
 *
 * @param jdbcUrl - String de conexão com o banco de dados. Configurada no koin.properties.
 * @param username - String com o nome do usuário. Configurada no koin.properties.
 * @param password - String com a senha. Configurada no koin.properties.
 */
class H2DatabaseConfig(jdbcUrl: String, username: String, password: String) {

	private val dataSource: DataSource

	init {
		Server.createPgServer().start()
		dataSource = HikariConfig().let { config ->
			config.jdbcUrl = jdbcUrl
			config.username = username
			config.password = password
			HikariDataSource(config)
		}
	}

	fun getDataSource(): DataSource {
		return dataSource
	}
}
