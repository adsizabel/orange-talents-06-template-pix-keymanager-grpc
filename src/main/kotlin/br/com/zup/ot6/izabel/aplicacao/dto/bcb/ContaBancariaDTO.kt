package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import br.com.zup.ot6.izabel.TipoConta
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
){}
