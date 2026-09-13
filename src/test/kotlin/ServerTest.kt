package ee.kpikka

import ee.kpikka.model.Sector
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import java.nio.file.Files
import kotlin.test.*

class ServerTest {

    @Test
    fun `root renders the form template`() = testApplication {
        configure()

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        assertContains(response.bodyAsText(), "<form")
    }

    @Test
    fun `stylesheet is served as a static resource`() = testApplication {
        configure()

        val response = client.get("/static/styles.css")

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "flex-direction: column")
    }

    @Test
    fun `sectors endpoint returns migrated sectors in hierarchical order`() = withTestApplication {
        val response = client.get("/sectors")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters())

        val sectors = Json.decodeFromString<List<Sector>>(response.bodyAsText())
        assertEquals(79, sectors.size)
        assertContains(sectors, Sector(271, "Aluminium and steel workboats", 97))

        val childrenByParent = sectors.groupBy(Sector::parentId)

        fun hierarchicallySorted(parentId: Int?): List<Sector> = childrenByParent[parentId]
            .orEmpty()
            .sortedBy { it.name.lowercase() }
            .flatMap { sector -> listOf(sector) + hierarchicallySorted(sector.id) }

        assertEquals(hierarchicallySorted(null), sectors)
    }

    private fun withTestApplication(test: suspend ApplicationTestBuilder.() -> Unit) {
        val database = Files.createTempFile("helmes-kotlin-test-", ".db")

        try {
            testApplication {
                configure {
                    this["database.jdbcUrl"] = "jdbc:sqlite:$database"
                }
                test()
            }
        } finally {
            Files.deleteIfExists(database)
        }
    }
}
