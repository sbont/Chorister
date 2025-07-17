package nl.stevenbontenbal.chorister.application

object ChoirContext {
    private val currentChoirId: ThreadLocal<Long?> = ThreadLocal<Long?>()

    fun setCurrentChoirId(choirId: Long) = currentChoirId.set(choirId)

    fun getCurrentChoirId(): Long? = currentChoirId.get()

    fun clear() = currentChoirId.remove()
}