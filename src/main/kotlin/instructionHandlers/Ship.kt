package instructionHandlers

import Shipment
import ShippingUpdate

class Ship : InstructionHandler {
    override fun handleInstruction(instructionSplit: List<String>, shipment: Shipment?) {
        try {
            shipment?.addUpdate(
                ShippingUpdate(
                    shipment,
                    instructionSplit[1].toLong(),
                    newStatus = Status.Shipped,
                    newDeliveryDate = instructionSplit[2].toLong()
                )
            )
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(
                "Invalid number values: '${instructionSplit[1]}' or '${instructionSplit[2]}' at line ${TrackingSimulator.instructionCount}",
                e
            )
        }
    }

}
