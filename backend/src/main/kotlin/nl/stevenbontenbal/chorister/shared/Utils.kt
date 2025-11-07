package nl.stevenbontenbal.chorister.shared

import java.util.Optional

fun <T: Any> T?.toOptional(): Optional<T> = Optional<T>.ofNullable(this)
fun <T> Optional<T>.toNullable(): T? = this.orElse(null)