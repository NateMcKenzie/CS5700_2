import androidx.compose.runtime.mutableStateListOf
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class TrackerViewerHelperTest {
    lateinit var shipment: Shipment

    @BeforeTest
    fun setup() {
        shipment = Shipment(Status.Created, "s1", 1652712855468)
        TrackingManager.addShipment(shipment)
    }

    @AfterTest
    fun clear() = TrackingManager.clearShipments()

    @Test
    fun defaultsTest() {
        val viewer = TrackerViewerHelper()
        assertEquals("", viewer.shipmentId)
        assertEquals(Status.Unknown, viewer.shipmentStatus)
        assertContentEquals(mutableStateListOf(), viewer.shipmentUpdateHistory)
        assertEquals(0, viewer.expectedShipmentDeliveryDate)
        assertEquals("", viewer.shipmentCurrentLocation)
        assertContentEquals(mutableStateListOf(), viewer.shipmentNotes)
    }

    @Test
    fun trackingTest() {
        val viewer = TrackerViewerHelper()
        viewer.trackShipment("s1")

        shipment.addUpdate(
            ShippingUpdate(
                shipment,
                1652712855468,
                newStatus = Status.Delivered,
                newDeliveryDate = 1652712875870
            )
        )
        shipment.addNote("This is a box")
        shipment.addUpdate(ShippingUpdate(shipment, 1652712855468, newLocation = "Last seen: Logan, UT"))

        assertEquals("s1", viewer.shipmentId)
        assertEquals(Status.Delivered, viewer.shipmentStatus)
        assertContentEquals(TrackingManager.findShipment("s1")?.updateHistory?.toSet(), viewer.shipmentUpdateHistory)
        assertEquals(1652712875870, viewer.expectedShipmentDeliveryDate)
        assertEquals("Last seen: Logan, UT", viewer.shipmentCurrentLocation)
        assertContentEquals(TrackingManager.findShipment("s1")?.notes, viewer.shipmentNotes)
    }

    @Test
    fun initTest() {
        val viewer = TrackerViewerHelper()
        viewer.trackShipment("s1")
        assertEquals("s1", viewer.shipmentId)
        assertEquals(Status.Created, viewer.shipmentStatus)
        assertContentEquals(TrackingManager.findShipment("s1")?.updateHistory, viewer.shipmentUpdateHistory)
        assertEquals(1652712855468, viewer.expectedShipmentDeliveryDate)
        assertEquals("Warehouse", viewer.shipmentCurrentLocation)
        assertContentEquals(TrackingManager.findShipment("s1")?.notes, viewer.shipmentNotes)
    }

    @Test
    fun delayedInitTest() {
        val viewer = TrackerViewerHelper()

        shipment.addUpdate(
            ShippingUpdate(
                shipment,
                1652712855468,
                newStatus = Status.Delivered,
                newDeliveryDate = 1652712875870
            )
        )
        shipment.addNote("This is a box")
        shipment.addUpdate(ShippingUpdate(shipment, 1652712855468, newLocation = "Last seen: Logan, UT"))

        viewer.trackShipment("s1")

        assertEquals("s1", viewer.shipmentId)
        assertEquals(Status.Delivered, viewer.shipmentStatus)
        assertContentEquals(TrackingManager.findShipment("s1")?.updateHistory?.toSet(), viewer.shipmentUpdateHistory)
        assertEquals(1652712875870, viewer.expectedShipmentDeliveryDate)
        assertEquals("Last seen: Logan, UT", viewer.shipmentCurrentLocation)
        assertContentEquals(TrackingManager.findShipment("s1")?.notes, viewer.shipmentNotes)
    }

    @Test
    fun stopTrackingTest() {
        val viewer = TrackerViewerHelper()
        viewer.trackShipment("s1")
        assertEquals("s1", viewer.shipmentId)
        viewer.stopTracking()
        assertEquals("No Longer Tracking", viewer.shipmentId)
    }

    @Test
    fun hallucinatedIdTest() {
        val viewer = TrackerViewerHelper()
        assertFalse{viewer.trackShipment("NeverHasBennARealShipmentId")}
        //Full disclosure: I submitted this
        //assertFailsWith<IllegalStateException> {
        //    viewer.trackShipment("NeverHasBeenARealShipmentId")
        //}
    }

    @Test
    fun stopTrackingNothingTest() {
        val viewer = TrackerViewerHelper()
        assertDoesNotThrow {
            viewer.stopTracking()
        }
    }

    @Test
    fun updateNothingTest() {
        val viewer = TrackerViewerHelper()
        assertDoesNotThrow {
            viewer.update()
        }
    }
}