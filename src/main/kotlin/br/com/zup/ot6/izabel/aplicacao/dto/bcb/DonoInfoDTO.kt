package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import br.com.zup.ot6.izabel.dominio.enums.BCBTipoPessoa
import com.fasterxml.jackson.annotation.JsonProperty

class DonoInfoDTO(
    @JsonProperty(value = "type")
    val tipo: BCBTipoPessoa,
    @JsonProperty(value = "name")
    val nome: String,
    @JsonProperty(value = "taxIdNumber")
    val cpf: String
) {

}