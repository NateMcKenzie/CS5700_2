package instructionHandlers

import Shipment
import ShippingUpdate

class UpdateLocation : InstructionHandler {
    override fun handleInstruction(instructionSplit: List<String>, shipment: Shipment?) {
        val remainder = instructionSplit.drop(2)
        var rejoined = ""
        for (part in remainder) {
            rejoined += "$part,"
        }
        rejoined = rejoined.dropLast(1)
        try {
            shipment?.addUpdate(ShippingUpdate(shipment, instructionSplit[1].toLong(), newLocation = rejoined))
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(
                "Invalid timestamp: '${instructionSplit[1]}' at line ${TrackingSimulator.instructionCount}",
                e
            )
        }
    }

}
