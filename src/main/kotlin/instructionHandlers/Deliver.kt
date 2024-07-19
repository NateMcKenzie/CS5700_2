package instructionHandlers

import Shipment
import ShippingUpdate
import TrackingSimulator

class Deliver : InstructionHandler {
    override fun handleInstruction(instructionSplit: List<String>, shipment: Shipment?) {
        try {
            shipment?.addUpdate(
                ShippingUpdate(
                    shipment,
                    instructionSplit[1].toLong(),
                    newStatus = Status.Delivered,
                    newDeliveryDate = instructionSplit[1].toLong()
                )
            )
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(
                "Invalid timestamp: '${instructionSplit[1]}' at line ${TrackingSimulator.instructionCount}",
                e
            )
        }
    }

}
