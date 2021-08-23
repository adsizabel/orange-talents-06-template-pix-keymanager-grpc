package br.com.zup.ot6.izabel.dominio.enums

import br.com.zup.ot6.izabel.aplicacao.dto.pix.ChavePixInfoDTO
import br.com.zup.ot6.izabel.aplicacao.dto.pix.ChavePixInfoDTO.Companion.paraResponseERP
import br.com.zup.ot6.izabel.aplicacao.excecoes.ClienteNaoEncontradoExcecao
import br.com.zup.ot6.izabel.aplicacao.integracoes.IntegracaoBCB
import br.com.zup.ot6.izabel.dominio.repositorios.ChavePixRepositorio
import br.com.zup.ot6.izabel.dominio.servicos.ChavePixService
import br.com.zup.ot6.izabel.dominio.servicos.impl.ChavePixServiceImpl
import io.grpc.Status.NOT_FOUND
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.hibernate.annotations.NotFound
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.validation.constraints.NotBlank

@Introspected
sealed class Filtro{
    abstract fun filtra(
        repositorio: ChavePixRepositorio,
        bcbCliente: IntegracaoBCB,
        service: ChavePixService
    ): ChavePixInfoDTO

    @Introspected
    data class PorPixIdEClienteId(
        @field:NotBlank val clienteId: String,
        @field:NotBlank val pixId: String,
    ): Filtro() {

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        fun pixIdUuid() = UUID.fromString(pixId)
        fun clienteIdUuid() = UUID.fromString(clienteId)

        override fun filtra(repositorio: ChavePixRepositorio, bcbCliente: IntegracaoBCB, service: ChavePixService
        ): ChavePixInfoDTO {
            val chavePix = repositorio.findByIdAndClienteId(pixIdUuid(), clienteIdUuid())
            if (!chavePix.isPresent) throw ClienteNaoEncontradoExcecao("Não foi possivel encontrar um cliente valido para o ID ${pixId}.")
            val dados = service.getDadosContaCliente(clienteId, chavePix.get().tipoConta)
            return paraResponseERP(chavePix.get(), dados)
        }
    }

    @Introspected
    data class PorChave(
        val chavePix: String,
        ): Filtro(){

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        override fun filtra(repositorio: ChavePixRepositorio, bcbCliente: IntegracaoBCB, service: ChavePixService
        ): ChavePixInfoDTO {
            TipoChaveValidador.isTamanhoValido(chavePix)

            val chavePixInfoDTO = if (repositorio.existsByChavePix(chavePix)) {
                LOGGER.info("Consultando chaves pix pela chave: ${chavePix}.")
                val chavePix = repositorio.findByChavePix(chavePix)
                val dadosConta = service.getDadosContaCliente(chavePix.clienteId.toString(), chavePix.tipoConta)
                ChavePixInfoDTO.paraResponseERP(chavePix, dadosConta)
            } else {
                LOGGER.info("Consultando chaves pix: ${chavePix} no BCB.")
                val chavePixResponse = bcbCliente.pesquisarChavePix(chavePix).body()
                    ?: throw ClienteNaoEncontradoExcecao("Cliente não encontrado")
                ChavePixInfoDTO.paraResponseBCB(chavePixResponse)
            }
            return chavePixInfoDTO
        }
    }

    @Introspected
    class Invalido(): Filtro(){
        override fun filtra(repositorio: ChavePixRepositorio, bcbCliente: IntegracaoBCB, service: ChavePixService
        ): ChavePixInfoDTO {
            throw IllegalArgumentException("Chave Pix Invalida ou não informada")
        }
    }

}
