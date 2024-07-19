abstract class InstructionStream : Subject{
    var nextInstruction: String = ""
        protected set
    private var observers: MutableList<Observer> = mutableListOf()
    abstract fun start()

    override fun subscribe(observer: Observer) {
        observers.add(observer)
    }

    override fun unsubscribe(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifySubscribers() {
        observers.forEach { it.update() }
    }
}