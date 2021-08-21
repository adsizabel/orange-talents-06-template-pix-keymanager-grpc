package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import br.com.zup.ot6.izabel.dominio.enums.BCBTipoChave
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class DetalhesChavePixResponse(
    @JsonProperty(value = "keyType")
    val tipoChave: BCBTipoChave,
    @JsonProperty(value = "key")
    val chave: String,
    @JsonProperty(value = "bankAccount")
    val contaBancaria: ContaBancariaDTO,
    @JsonProperty(value = "owner")
    val dono: DonoInfoDTO,
    @JsonProperty(value = "createdAt")
    val criadoEm: LocalDateTime
) {

}
