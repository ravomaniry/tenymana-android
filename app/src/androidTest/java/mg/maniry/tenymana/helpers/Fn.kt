package mg.maniry.tenymana.helpers

private class Impl<T>(
    private val implementations: MutableList<Impl<T>>,
    private val fn: (args: List<Any?>) -> T,
    private val once: Boolean = false
) {
    fun remove() {
        implementations.remove(this)
    }

    fun invoke(args: List<Any?> = listOf()): T {
        if (once) {
            remove()
        }
        return fn(args)
    }
}

class Fn<T> {
    private val arguments = mutableListOf<List<Any?>>()
    private val implementations = mutableListOf<Impl<T>>()

    operator fun invoke(vararg p: Any?): T {
        arguments.add(p.toList())
        val impl = if (implementations.isEmpty()) null else implementations[0]
        @Suppress("unchecked_cast")
        return impl?.invoke(p.toList()) as T
    }

    val calls: List<Any> get() = arguments
    val calledTimes: Int get() = arguments.size
    val called: Boolean get() = arguments.isNotEmpty()
    val notCalled: Boolean get() = !called

    fun mockImplementation(fn: (List<Any?>) -> T): Fn<T> {
        implementations.add(Impl(implementations, fn))
        return this
    }

    fun mockImplementationOnce(fn: (List<Any?>) -> T): Fn<T> {
        implementations.add(
            Impl(
                implementations,
                fn,
                true
            )
        )
        return this
    }

    fun mockReturnValue(value: T): Fn<T> {
        return mockImplementation { value }
    }

    fun mockReturnValueOnce(value: T): Fn<T> {
        return mockImplementationOnce { value }
    }

    fun calledWith(vararg a: Any?): Boolean {
        if (arguments.contains(a.toList())) {
            return true
        }
        println("Expected but not called ${a.toList()}\nCalls: $arguments")
        return false
    }

    fun clear() {
        arguments.removeAll { true }
    }

    fun reset() {
        clear()
        implementations.removeAll { true }
    }
}
