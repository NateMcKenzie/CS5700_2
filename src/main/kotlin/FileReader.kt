import java.io.File
import java.util.*
import kotlin.concurrent.timer

class FileReader(
    fileName: String,
    private val period: Long = 1000L,
) : InstructionStream(){
    private val file = File(fileName).bufferedReader()
    private lateinit var timer: Timer
    var lineNumber = 0
        private set

    override fun start() {
        timer = timer(initialDelay = 0L, period = period) {
            readInstruction()
        }
    }

    private fun readInstruction() {
        //Credit to Claude AI for this kotlin idiom (if null do expected, else shut it down)
        file.readLine()?.also { line ->
            nextInstruction = line
            lineNumber++
            notifySubscribers()
        } ?: run {
            timer.cancel()
        }
    }

}