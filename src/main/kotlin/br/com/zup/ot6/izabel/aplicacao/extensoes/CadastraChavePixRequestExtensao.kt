package br.com.zup.ot6.izabel.aplicacao.extensoes

import br.com.zup.ot6.izabel.CadastrarChavePixRequest
import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO

fun CadastrarChavePixRequest.paraDTO(): CadastrarChavePixDTO {

    return CadastrarChavePixDTO(
        clienteId = this.idCliente,
        tipoChavePix = this.tipoChavePix,
        chavePix = this.chavePix,
        tipoConta = tipoConta
    )

}