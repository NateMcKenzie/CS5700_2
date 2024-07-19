package instructionHandlers

import Shipment
import TrackingManager

class AddNote : InstructionHandler {
    override fun handleInstruction(instructionSplit: List<String>, shipment: Shipment?) {
        val remainder = instructionSplit.drop(2)
        var rejoined = ""
        for (part in remainder) {
            rejoined += "$part,"
        }
        rejoined = rejoined.dropLast(1)
        shipment?.addNote(rejoined)
    }
}
