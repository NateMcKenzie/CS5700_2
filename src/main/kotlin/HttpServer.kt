import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

class HttpServer : InstructionStream(){
    override fun start() {
        embeddedServer(Netty, port = 8000) {
            routing {
                get("/") {
                    call.respondFile(File("res/index.html"))
                }
                post ( "/update" ) {
                    val updateString = call.receiveParameters()["instruction"]
                    updateString?.let {
                        nextInstruction = updateString
                        notifySubscribers()
                    }
                    call.respondRedirect("/")
                }
            }
        }.start(wait = false)
    }

    private fun processInput() {

    }
}