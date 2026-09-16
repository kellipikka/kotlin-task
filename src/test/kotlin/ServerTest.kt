package ee.kpikka

import ee.kpikka.db.table.FormResponseTable
import ee.kpikka.db.withTransaction
import ee.kpikka.model.Sector
import ee.kpikka.repository.FormResponseRepository
import ee.kpikka.service.FormService
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.nio.file.Files
import kotlin.test.*

class ServerTest {

    @Test
    fun `root renders the form template`() = withTestApplication {
        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        assertContains(response.bodyAsText(), "<form")
    }

    @Test
    fun `parent sectors are disabled and explain that a sub-sector is required`() = withTestApplication {
        val page = client.get("/").bodyAsText()

        assertEquals("disabled", attribute(optionTag(page, 1), "disabled"))
        assertContains(page, "Categories with sub-sectors cannot be selected")
        assertNull(attribute(optionTag(page, 271), "disabled"))
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
    fun `unexpected errors are logged without exposing details to the user`() = withTestApplication {
        application {
            routing {
                get("/unexpected-error") {
                    error("Sensitive internal detail")
                }
            }
        }

        val response = client.get("/unexpected-error")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("An unexpected error occurred. Please try again later.", response.bodyAsText())
        assertFalse(response.bodyAsText().contains("Sensitive internal detail"))
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
    fun `database requires agreement to terms`() = withTestApplication {
        startApplication()

        assertNotNull(
            databaseFailure(
                "INSERT INTO form_responses (name, agreed_to_terms) VALUES ('Missing agreement', NULL)"
            )
        )
        assertNotNull(
            databaseFailure(
                "INSERT INTO form_responses (name, agreed_to_terms) VALUES ('Declined agreement', 0)"
            )
        )
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
                append("terms", "1")
            },
            sessionCookie
        )

        val page = getForm(sessionCookie)
        assertEquals("Updated name", attribute(openingTag(page, "name"), "value"))
        assertNull(attribute(optionTag(page, 271), "selected"))
        assertEquals("selected", attribute(optionTag(page, 576), "selected"))
        assertEquals("checked", attribute(openingTag(page, "terms"), "checked"))
        assertEquals(1, withTransaction { FormResponseTable.selectAll().count() })
    }

    @Test
    fun `stale session creates a new response and replaces its cookie`() = withTestApplication {
        submitForm(validFormParameters("First"))
        submitForm(validFormParameters("Second"))
        val staleCookie = sessionCookieFrom(submitForm(validFormParameters("Third")))

        withTransaction { exec("DELETE FROM form_responses") }

        val replacement = submitForm(validFormParameters("Replacement"), staleCookie)
        val replacementCookie = sessionCookieFrom(replacement)

        assertNotEquals(replacementCookie, staleCookie)
        assertEquals("Replacement", FormResponseRepository.getResponse(1)?.name)
    }

    @Test
    fun `form values are isolated between sessions`() = withTestApplication {
        val adaSession = sessionCookieFrom(submitForm(validFormParameters("Ada")))
        val graceSession = sessionCookieFrom(submitForm(validFormParameters("Grace")))

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
                append("terms", "1")
            }
        )

        assertEquals(listOf(271), FormResponseRepository.getResponse(1)?.sectorIds)
    }

    @Test
    fun `submitting a parent sector rejects the entire form`() = withTestApplication {
        val response = submitInvalidForm(
            parameters {
                append("name", "Ada")
                append("sectors", "1")
                append("sectors", "271")
                append("terms", "1")
            }
        )

        assertContains(response.bodyAsText(), "One or more selected sectors are invalid")
        assertNull(FormResponseRepository.getResponse(1))
    }

    @Test
    fun `submitting a malformed sector rejects the entire form`() = withTestApplication {
        val response = submitInvalidForm(
            parameters {
                append("name", "Ada")
                append("sectors", "271")
                append("sectors", "not-an-id")
                append("terms", "1")
            }
        )

        assertContains(response.bodyAsText(), "Sector IDs must be integers")
        assertNull(FormResponseRepository.getResponse(1))
    }

    @Test
    fun `submitting more than one hundred sectors rejects the form before deduplication`() = withTestApplication {
        val response = submitInvalidForm(
            parameters {
                append("name", "Ada")
                repeat(101) { append("sectors", "271") }
                append("terms", "1")
            }
        )

        assertContains(response.bodyAsText(), "No more than 100 sectors can be selected")
        assertNull(FormResponseRepository.getResponse(1))
    }

    private suspend fun ApplicationTestBuilder.submitForm(
        formParameters: Parameters,
        sessionCookie: String? = null,
    ) = postForm(formParameters, sessionCookie).also { response ->
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/", response.headers[HttpHeaders.Location])
    }

    private suspend fun ApplicationTestBuilder.submitInvalidForm(
        formParameters: Parameters,
    ) = postForm(formParameters).also { response ->
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    private suspend fun ApplicationTestBuilder.postForm(
        formParameters: Parameters,
        sessionCookie: String? = null,
    ) = createClient {
        followRedirects = false
    }.submitForm("/form", formParameters) {
        sessionCookie?.let { header(HttpHeaders.Cookie, it) }
    }

    private fun validFormParameters(name: String) = parameters {
        append("name", name)
        append("sectors", "271")
        append("terms", "1")
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

    private suspend fun databaseFailure(statement: String): Throwable? = try {
        withTransaction { exec(statement) }
        null
    } catch (cause: Throwable) {
        cause
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
