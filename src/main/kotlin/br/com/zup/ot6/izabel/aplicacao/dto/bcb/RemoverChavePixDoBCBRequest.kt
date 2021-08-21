package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import com.fasterxml.jackson.annotation.JsonProperty

data class RemoverChavePixDoBCBRequest(
    @JsonProperty("key")
    val chavePix: String,
    @JsonProperty("participant")
    val participant: String = ITAU_UNIBANCO_ISPB
) {

    companion object{
            const val ITAU_UNIBANCO_ISPB = "60701190"
        }


}
