package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import br.com.zup.ot6.izabel.dominio.enums.BCBTipoConta
import com.fasterxml.jackson.annotation.JsonProperty

data class ContaBancariaDTO(
    @JsonProperty(value = "participant")
    val ispb: String,
    @JsonProperty(value = "branch")
    val agencia: String,
    @JsonProperty(value = "accountNumber")
    val numero: String,
    @JsonProperty(value = "accountType")
    val tipo: BCBTipoConta
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContaBancariaDTO

        if (ispb != other.ispb) return false
        if (agencia != other.agencia) return false
        if (numero != other.numero) return false
        if (tipo != other.tipo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ispb.hashCode()
        result = 31 * result + agencia.hashCode()
        result = 31 * result + numero.hashCode()
        result = 31 * result + tipo.hashCode()
        return result
    }
}
