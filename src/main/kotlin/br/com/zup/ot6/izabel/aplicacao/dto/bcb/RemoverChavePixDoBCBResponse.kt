package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class RemoverChavePixDoBCBResponse(
    @JsonProperty("key")
    val chavePix: String,
    @JsonProperty("participant")
    val donoChave: String,
    @JsonProperty("deletedAt")
    val deletadoEm: LocalDateTime,
) {

}
