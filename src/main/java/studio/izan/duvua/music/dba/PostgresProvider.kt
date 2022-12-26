package studio.izan.duvua.music.dba

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import studio.izan.duvua.music.types.PostgresOptions

class PostgresProvider(
    private val dbOptions: PostgresOptions
) {
    private val logger = LoggerFactory.getLogger("PostgresProvider")
        private fun getDataSource(): HikariDataSource {
            logger.info("Connecting to datasource...")

            val hikariConfig = HikariConfig().apply {
                jdbcUrl = "jdbc:postgresql://${dbOptions.host}/${dbOptions.databaseName}"
                driverClassName = "org.postgresql.Driver"
                username = dbOptions.username
                password = dbOptions.password
                maximumPoolSize = dbOptions.maxPoolSize ?: 10
                isAutoCommit = false
                validate()
            }

            val dataSource = HikariDataSource(hikariConfig)
            hikariConfig.dataSource = dataSource
            logger.info("Connected to datasource")
            return dataSource
    }

    public val connection = getDataSource().connection
}