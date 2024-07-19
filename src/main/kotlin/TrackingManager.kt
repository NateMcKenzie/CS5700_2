import instructionHandlers.*

object TrackingManager : Observer {
    private val shipments = mutableSetOf<Shipment>()
    private val instructionMap = mapOf(
        "created" to CreateShipment(),
        "shipped" to Ship(),
        "location" to UpdateLocation(),
        "delayed" to Delay(),
        "noteadded" to AddNote(),
        "lost" to Lose(),
        "canceled" to Cancel(),
        "delivered" to Deliver()
    )
    private lateinit var instructionStream: InstructionStream

    fun findShipment(id: String) = shipments.find {
        it.id == id
    }

    fun addShipment(shipment: Shipment) {
        shipments.add(shipment)
    }

    fun runSimulation(instructionStream: InstructionStream) {
        this.instructionStream = instructionStream
        instructionStream.subscribe(this)
        instructionStream.start()
    }

    override fun update() {
        val splits = instructionStream.nextInstruction.split(',')
        if (splits.size < 2) {
            print("Invalid input: ${instructionStream.nextInstruction}. Skipping")
            return
        }
        val handler = instructionMap[splits[0]] ?: run {
            print("${splits[0]} not a known instruction. Skipping")
            return
        }
        val shipment = findShipment(splits[1])
        handler.handleInstruction(splits.drop(1), shipment)
    }

    fun clearShipments() = shipments.clear()
}
