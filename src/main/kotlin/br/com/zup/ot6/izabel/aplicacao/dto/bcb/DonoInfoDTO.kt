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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DonoInfoDTO

        if (tipo != other.tipo) return false
        if (nome != other.nome) return false
        if (cpf != other.cpf) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tipo.hashCode()
        result = 31 * result + nome.hashCode()
        result = 31 * result + cpf.hashCode()
        return result
    }
}