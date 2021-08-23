package br.com.zup.ot6.izabel.aplicacao.dto.pix

import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import br.com.zup.ot6.izabel.aplicacao.dto.bcb.DetalhesChavePixResponse
import br.com.zup.ot6.izabel.aplicacao.dto.erp.DadosDaContaResponse
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import br.com.zup.ot6.izabel.dominio.enums.BCBTipoConta
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfoDTO(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipoChave: TipoChavePix,
    val valorChave: String,
    val titular: TitularDTO,
    val contaBancaria: ContaBancariaDTO,
    val dataCriacao: LocalDateTime,
){
    companion object {
        fun paraResponseERP(chavePix: ChavePix, dadosConta: DadosDaContaResponse): ChavePixInfoDTO{
            return ChavePixInfoDTO(
                pixId = chavePix.id,
                clienteId = chavePix.clienteId,
                tipoChave = chavePix.tipoChavePix,
                valorChave = chavePix.chavePix,
                titular = TitularDTO(
                    nome = dadosConta.titular.nome,
                    cpf = dadosConta.titular.cpf
                ),
                contaBancaria = ContaBancariaDTO(
                    nomeInstituicao = dadosConta.instituicao.nome,
                    agencia = dadosConta.agencia,
                    numero = dadosConta.numero,
                    tipo = dadosConta.tipo
                ),
                dataCriacao = chavePix.criadoEm
            )
        }

        fun paraResponseBCB(chavePixResponse: DetalhesChavePixResponse): ChavePixInfoDTO {
            return ChavePixInfoDTO(
                tipoChave = TipoChavePix.valueOf(chavePixResponse.tipoChave.descricao),
                valorChave = chavePixResponse.chave,
                titular = TitularDTO(
                    nome = chavePixResponse.dono.nome,
                    cpf = chavePixResponse.dono.cpf
                ),
                contaBancaria = ContaBancariaDTO(
                    nomeInstituicao = chavePixResponse.contaBancaria.ispb, //TODO consultar ISPB da Relação de participantes do STR
                    agencia = chavePixResponse.contaBancaria.agencia,
                    numero = chavePixResponse.contaBancaria.numero,
                    tipo = when (chavePixResponse.contaBancaria.tipo) {
                        BCBTipoConta.CACC -> TipoConta.CONTA_CORRENTE.name
                        BCBTipoConta.SVGS -> TipoConta.CONTA_POUPANCA.name
                    }
                ),
                dataCriacao = chavePixResponse.criadoEm
            )
        }
    }
}

data class ContaBancariaDTO(
    val nomeInstituicao: String,
    val agencia: String,
    val numero: String,
    val tipo: String
){}

data class TitularDTO(
    val nome: String,
    val cpf: String
){}