package instructionHandlers

import Shipment
import ShippingUpdate
import TrackingSimulator

class Delay : InstructionHandler {
    override fun handleInstruction(instructionSplit: List<String>, shipment: Shipment?) {
        try {
            shipment?.addUpdate(
                ShippingUpdate(
                    shipment,
                    instructionSplit[1].toLong(),
                    newDeliveryDate = instructionSplit[2].toLong(),
                    newStatus = Status.Delayed
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
