package instructionHandlers

import Shipment
import ShippingUpdate
import TrackingSimulator
import javax.sound.midi.Track

class Cancel : InstructionHandler {
    override fun handleInstruction(instructionSplit: List<String>, shipment: Shipment?) {
        try {
            shipment?.addUpdate(ShippingUpdate(shipment, instructionSplit[1].toLong(), Status.Canceled))
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(
                "Invalid timestamp: '${instructionSplit[1]}' at line ${TrackingSimulator.instructionCount}",
                e
            )
        }
    }
}
