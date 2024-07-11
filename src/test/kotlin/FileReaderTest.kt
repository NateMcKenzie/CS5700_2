import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.FileNotFoundException
import kotlin.test.*

class FileReaderTest : Observer{
    private val instructionList = mutableListOf<String>()
    private val observedReader = FileReader("res/fileReaderTest.txt", period=5L)
    init {
        observedReader.subscribe(this)
    }

    @Test
    fun constructorGoodTest() {
        assertDoesNotThrow {
            val reader = FileReader("res/fileReaderTest.txt")
        }
    }

    @Test
    fun constructorBadTest() {
        assertFailsWith<FileNotFoundException> {
            val reader = FileReader("")
        }
        assertFailsWith<FileNotFoundException> {
            val reader = FileReader("res/not_a_real_file_at_all.txt")
        }
    }

    @Test
    fun endOfFileTest() = runBlocking{
        val reader = FileReader("res/short.txt", period=5L)
        reader.start()
        delay(6)
        assertEquals("created,s1,1652712855468", reader.nextInstruction)
    }

    @Test
    fun subscribeTest() = runBlocking {
        val reader = FileReader("res/fileReaderTest.txt", period=5L)
        val observer = ObserverTestHelper()
        reader.subscribe(observer)
        assertFalse { observer.triggered }
        reader.start()
        delay(6)
        assertTrue { observer.triggered }
    }

    @Test
    fun unsubscribeTest() = runBlocking {
        val reader = FileReader("res/fileReaderTest.txt", period=5L)
        val observer = ObserverTestHelper()
        reader.subscribe(observer)
        reader.unsubscribe(observer)
        assertFalse { observer.triggered }
        reader.start()
        delay(6)
        assertFalse { observer.triggered }
    }

    @Test
    fun contentTest() = runBlocking{
        observedReader.start()
        delay(30L)
        print(instructionList)
        assertContentEquals(listOf(
        "created,s1,1652712855468",
        "created,s2,1652712855468",
        "shipped,s1,1652712855468,1652713940874",
        "shipped,s2,1652712855468,1652713940874",
        "lost,s1,1652712855468"), instructionList)
    }

    override fun update() {
       instructionList.add(observedReader.nextInstruction)
    }
}