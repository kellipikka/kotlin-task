package ee.kpikka

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

@Serializable
data class FormSession(val formResponseId: Int)

fun Application.configureSession() {
    val signingKey = environment.config
        .property("session.signingKey")
        .getString()
        .hexToByteArray()

    install(Sessions) {
        cookie<FormSession>("form_session") {
            cookie.path = "/"
            cookie.httpOnly = true
            cookie.secure = true
            cookie.sameSite = SameSite.Lax
            sendOnlyIfModified = true

            transform(
                SessionTransportTransformerMessageAuthentication(signingKey)
            )
        }
    }
}
