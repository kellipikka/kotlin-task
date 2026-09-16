package ee.kpikka

import ee.kpikka.model.Sector
import ee.kpikka.repository.FormResponseRepository
import ee.kpikka.service.FormService
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ServerTest {

    @Test
    fun `root renders the form template`() = withTestApplication {
        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        assertContains(response.bodyAsText(), "<form")
    }

    @Test
    fun `missing form response falls back to a blank form`() = withTestApplication {
        startApplication()

        val response = FormService.getExistingOrNewFormResponse(Int.MAX_VALUE)

        assertNull(response.id)
        assertEquals("", response.name)
        assertEquals(0, response.agreedToTerms)
        assertEquals(emptyList(), response.sectorIds)
    }

    @Test
    fun `stylesheet is served as a static resource`() = withTestApplication {
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

    @Test
    fun `submitted form values are restored for the session`() = withTestApplication {
        val submission = submitForm(
            parameters {
                append("name", "Ada Lovelace")
                append("sectors", "271")
                append("sectors", "576")
                append("terms", "1")
            }
        )

        val page = getForm(sessionCookieFrom(submission))

        assertEquals("Ada Lovelace", attribute(openingTag(page, "name"), "value"))
        assertEquals("selected", attribute(optionTag(page, 271), "selected"))
        assertEquals("selected", attribute(optionTag(page, 576), "selected"))
        assertEquals("checked", attribute(openingTag(page, "terms"), "checked"))
    }

    @Test
    fun `resubmitting the form replaces values for the session`() = withTestApplication {
        val firstSubmission = submitForm(
            parameters {
                append("name", "First name")
                append("sectors", "271")
                append("terms", "1")
            }
        )
        val sessionCookie = sessionCookieFrom(firstSubmission)

        submitForm(
            parameters {
                append("name", "Updated name")
                append("sectors", "576")
            },
            sessionCookie
        )

        val page = getForm(sessionCookie)
        assertEquals("Updated name", attribute(openingTag(page, "name"), "value"))
        assertNull(attribute(optionTag(page, 271), "selected"))
        assertEquals("selected", attribute(optionTag(page, 576), "selected"))
        assertNull(attribute(openingTag(page, "terms"), "checked"))
    }

    @Test
    fun `form values are isolated between sessions`() = withTestApplication {
        val adaSession = sessionCookieFrom(submitForm(parameters { append("name", "Ada") }))
        val graceSession = sessionCookieFrom(submitForm(parameters { append("name", "Grace") }))

        assertEquals("Ada", attribute(openingTag(getForm(adaSession), "name"), "value"))
        assertEquals("Grace", attribute(openingTag(getForm(graceSession), "name"), "value"))
    }

    @Test
    fun `submitting a sector more than once stores it once`() = withTestApplication {
        submitForm(
            parameters {
                append("name", "Ada")
                append("sectors", "271")
                append("sectors", "271")
            }
        )

        assertEquals(listOf(271), FormResponseRepository.getResponse(1)?.sectorIds)
    }

    private suspend fun ApplicationTestBuilder.submitForm(
        formParameters: Parameters,
        sessionCookie: String? = null,
    ) = createClient {
        followRedirects = false
    }.submitForm("/form", formParameters) {
        sessionCookie?.let { header(HttpHeaders.Cookie, it) }
    }.also { response ->
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/", response.headers[HttpHeaders.Location])
    }

    private suspend fun ApplicationTestBuilder.getForm(sessionCookie: String): String =
        client.get("/") {
            header(HttpHeaders.Cookie, sessionCookie)
        }.bodyAsText()

    private fun sessionCookieFrom(response: HttpResponse): String =
        response.headers.getAll(HttpHeaders.SetCookie)
            .orEmpty()
            .single { it.startsWith("form_session=") }
            .substringBefore(';')

    private fun openingTag(html: String, id: String): String =
        requireNotNull(Regex("<input\\b[^>]*\\bid=\"${Regex.escape(id)}\"[^>]*>").find(html)?.value) {
            "Could not find <input> with id '$id'"
        }

    private fun optionTag(html: String, value: Int): String =
        requireNotNull(Regex("<option\\b[^>]*\\bvalue=\"$value\"[^>]*>").find(html)?.value) {
            "Could not find <option> with value '$value'"
        }

    private fun attribute(tag: String, name: String): String? =
        Regex("\\b${Regex.escape(name)}(?:=\"([^\"]*)\")?")
            .find(tag)
            ?.groupValues
            ?.get(1)
            ?.ifEmpty { name }

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
