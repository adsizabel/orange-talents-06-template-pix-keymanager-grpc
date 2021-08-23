package br.com.zup.ot6.izabel.aplicacao.extensoes

import br.com.zup.ot6.izabel.CarregaChavePixRequest
import br.com.zup.ot6.izabel.dominio.enums.Filtro

fun CarregaChavePixRequest.paraDTO(): Filtro {

    return when (filtroCase) {
        CarregaChavePixRequest.FiltroCase.PIXID -> pixId.let { Filtro.PorPixIdEClienteId(clienteId = it.clienteId, pixId = it.pixId) }
        CarregaChavePixRequest.FiltroCase.CHAVE -> Filtro.PorChave(chave)
        CarregaChavePixRequest.FiltroCase.FILTRO_NOT_SET -> Filtro.Invalido()
    }
}