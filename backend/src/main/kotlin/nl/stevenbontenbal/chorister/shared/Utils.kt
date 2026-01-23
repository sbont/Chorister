package nl.stevenbontenbal.chorister.shared

import arrow.core.Either
import arrow.core.Option
import java.util.Optional

fun <T: Any> T?.toOptional(): Optional<T> = Optional<T>.ofNullable(this)

fun <T> Optional<T>.toNullable(): T? = this.orElse(null)

fun <L, R> R?.nullableToEither(ifNull: () -> L): Either<L, R> =
    Option.fromNullable(this).toEither(ifNull)

fun <L, R> Optional<R>.optionalToEither(ifNull: () -> L): Either<L, R> =
    Option.fromNullable(this.orElse(null)).toEither(ifNull)
