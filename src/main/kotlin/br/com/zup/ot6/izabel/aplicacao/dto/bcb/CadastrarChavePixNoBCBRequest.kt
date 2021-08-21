package br.com.zup.ot6.izabel.aplicacao.dto.bcb

import br.com.zup.ot6.izabel.aplicacao.dto.erp.DadosDaContaResponse
import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO
import br.com.zup.ot6.izabel.dominio.enums.BCBTipoChave
import br.com.zup.ot6.izabel.dominio.enums.BCBTipoConta
import br.com.zup.ot6.izabel.dominio.enums.BCBTipoPessoa
import br.com.zup.ot6.izabel.dominio.enums.TipoChaveValidador
import com.fasterxml.jackson.annotation.JsonProperty

data class CadastrarChavePixNoBCBRequest(
    @JsonProperty(value = "keyType")
    val tipoChave: BCBTipoChave,
    @JsonProperty(value = "key")
    val chave: String,
    @JsonProperty(value = "bankAccount")
    val contaBancaria: ContaBancariaDTO,
    @JsonProperty(value = "owner")
    val dono: DonoInfoDTO
){
    companion object {
        const val ITAU_UNIBANCO_ISPB = "60701190"

        fun criarChave(request: CadastrarChavePixDTO, dadosConta: DadosDaContaResponse) = CadastrarChavePixNoBCBRequest(
            tipoChave =  BCBTipoChave.comDescricao((request.tipoChavePix.name)),
            chave = request.chavePix,
            contaBancaria = ContaBancariaDTO(
                ispb = ITAU_UNIBANCO_ISPB,
                agencia = dadosConta.agencia,
                numero = dadosConta.numero,
                tipo = BCBTipoConta.comDescricao(dadosConta.tipo)
            ),
            dono = DonoInfoDTO(
                tipo = BCBTipoPessoa.NATURAL_PERSON,
                nome = dadosConta.titular.nome,
                cpf = dadosConta.titular.cpf
            )
        )
    }

}
