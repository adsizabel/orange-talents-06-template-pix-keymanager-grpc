package br.com.zup.ot6.izabel.aplicacao.extensoes

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.fromGoogleTimestamp(): Timestamp {
    val instant: Instant = this.toInstant(ZoneOffset.UTC)
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}