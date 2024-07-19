import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.schedule

@Composable
@Preview
fun App() {
    val shipmentHelpers = remember { mutableStateListOf<TrackerViewerHelper>() }
    var idInput by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf("") }
    val heading = TextStyle(
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 2.em,
        textDecoration = TextDecoration.Underline
    )
    val subheading =
        TextStyle(color = Color.White, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
    val body = TextStyle(color = Color.White)

    MaterialTheme {
        Column {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextField(idInput, onValueChange = {
                    idInput = it
                })
                Spacer(modifier = Modifier.weight(0.1f))
                Button(onClick = {
                    shipmentHelpers.forEach {
                        if (it.shipmentId == idInput)
                            return@Button
                    }
                    val newHelper = TrackerViewerHelper()
                    if (newHelper.trackShipment(idInput))
                        shipmentHelpers.add(newHelper)
                    else {
                        toastMessage = "$idInput not found."
                        Timer().schedule(delay = 3000) {
                            toastMessage = ""
                        }
                    }
                }) {
                    Text("Track")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            LazyColumn {
                items(shipmentHelpers, key = { it.shipmentId }) {
                    Card(
                        modifier = Modifier.padding(3.dp).border(2.dp, Color.Black).background(Color.DarkGray)
                    ) {
                        Column(modifier = Modifier.padding(3.dp).background(Color.DarkGray)) {
                            Row {
                                Spacer(Modifier.weight(1f))
                                Text(it.shipmentId, style = heading, modifier = Modifier.padding(2.dp))
                                Spacer(Modifier.weight(1f))
                                Button(onClick = {
                                    shipmentHelpers.remove(it)
                                    it.stopTracking()
                                }) { Text("X") }
                            }
                            Text(it.shipmentStatus.toString(), style = body, modifier = Modifier.padding(2.dp))
                            Text(it.shipmentCurrentLocation, style = body, modifier = Modifier.padding(2.dp))
                            Text(
                                stampConvert(it.expectedShipmentDeliveryDate),
                                style = body,
                                modifier = Modifier.padding(2.dp)
                            )
                            Column {
                                if (it.shipmentNotes.isNotEmpty()) {
                                    Text(
                                        "Notes",
                                        style = subheading,
                                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 7.dp)
                                    )
                                    it.shipmentNotes.forEach { note ->
                                        Text(note, style = body, modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }
                            Column {
                                if (it.shipmentUpdateHistory.isNotEmpty()) {
                                    Text(
                                        "Status Updates",
                                        style = subheading,
                                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 7.dp)
                                    )
                                    it.shipmentUpdateHistory.forEach { update ->
                                        if (update.previousStatus != update.newStatus)
                                            Text(
                                                "${stampConvert(update.timestamp)}\nShipment went from ${update.previousStatus} to ${update.newStatus}",
                                                style = body,
                                                modifier = Modifier.padding(2.dp)
                                            )
                                        if (update.previousLocation != update.newLocation)
                                            Text(
                                                "${stampConvert(update.timestamp)}\nShipment arrived from ${update.previousLocation} to ${update.newLocation}",
                                                style = body,
                                                modifier = Modifier.padding(2.dp)
                                            )
                                        if (update.previousDeliveryDate != update.newDeliveryDate)
                                            Text(
                                                "${stampConvert(update.timestamp)}\nShipment delivery changed from ${
                                                    stampConvert(
                                                        update.previousDeliveryDate
                                                    )
                                                } to ${stampConvert(update.newDeliveryDate)}",
                                                style = body,
                                                modifier = Modifier.padding(2.dp)
                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (toastMessage.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    toastMessage,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(5.dp).background(color = Color.Black)
                        .padding(2.dp),
                    style = TextStyle(color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 2.em)
                )
            }
        }
    }
}

fun stampConvert(timestamp: Long): String {
    if (timestamp < 0) {
        throw IllegalArgumentException("Refusing to convert negative timestamp due to inconsistent behavior")
    }
    val instant = Instant.ofEpochMilli(timestamp)
    return instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM/dd/yy hh:mma"))
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
        TrackingSimulator.runSimulation("res/provided.txt")
    }
}
