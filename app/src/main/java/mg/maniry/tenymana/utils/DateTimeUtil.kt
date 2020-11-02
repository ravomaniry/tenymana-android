package mg.maniry.tenymana.utils

import java.util.*

interface DateTimeUtil {
    val nowTst: Long
}

class DateTimeUtilImpl : DateTimeUtil {
    override val nowTst: Long get() = Date().time
}
