package br.com.zup.ot6.izabel.aplicacao.extensoes

import br.com.zup.ot6.izabel.CarregaChavePixResponse
import br.com.zup.ot6.izabel.aplicacao.dto.pix.ChavePixInfoDTO

class CarregaChavePixReponseConverter {

    fun converter(chaveInfo: ChavePixInfoDTO): CarregaChavePixResponse{
        return CarregaChavePixResponse.newBuilder()
            .setClienteId(chaveInfo.clienteId.toString() ?: "")
            .setPixId(chaveInfo.pixId.toString() ?: "")
            .setChave(CarregaChavePixResponse.ChavePix
                .newBuilder()
                .setTipo(br.com.zup.ot6.izabel.TipoChavePix.valueOf(chaveInfo.tipoChave.name))
                .setChave(chaveInfo.valorChave)
                .setConta(CarregaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipo(br.com.zup.ot6.izabel.TipoConta.valueOf(chaveInfo.contaBancaria.tipo))
                    .setInstituicao(chaveInfo.contaBancaria.nomeInstituicao)
                    .setTitular(chaveInfo.titular.nome)
                    .setCpf(chaveInfo.titular.cpf)
                    .setAgencia(chaveInfo.contaBancaria.agencia)
                    .setNumeroConta(chaveInfo.contaBancaria.numero)
                    .build()
                )
                .setCriadaEm(chaveInfo.dataCriacao.fromGoogleTimestamp()))
            .build()
    }
}