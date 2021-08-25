package br.com.zup.ot6.izabel.aplicacao.dto.pix

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class ConsultaChavePixDTO(
    @field:NotNull
    @field:NotBlank
    val clienteId: String,
    @field:NotNull
    @field:NotBlank
    val pixId: String,
)