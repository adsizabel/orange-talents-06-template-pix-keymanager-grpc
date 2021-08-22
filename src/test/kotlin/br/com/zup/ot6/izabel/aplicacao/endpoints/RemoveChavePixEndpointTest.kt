package br.com.zup.ot6.izabel.aplicacao.endpoints

import br.com.zup.ot6.izabel.*
import br.com.zup.ot6.izabel.aplicacao.dto.bcb.RemoverChavePixDoBCBRequest
import br.com.zup.ot6.izabel.aplicacao.dto.bcb.RemoverChavePixDoBCBResponse
import br.com.zup.ot6.izabel.aplicacao.integracoes.IntegracaoBCB
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import br.com.zup.ot6.izabel.dominio.repositorios.ChavePixRepositorio
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChavePixEndpointTest(
    val repositorio: ChavePixRepositorio,
    val grpc: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub
    ) {

    @Inject
    lateinit var bcbClient: IntegracaoBCB

    lateinit var OBJETO_SALVO: ChavePix

    companion object{
        val CLIENTE_ID = UUID.randomUUID()
    }

    @MockBean(IntegracaoBCB::class)
    fun bcbClient(): IntegracaoBCB{
        return Mockito.mock(IntegracaoBCB::class.java)
    }

    @BeforeEach
    fun setup(){
        OBJETO_SALVO = repositorio.save(
            ChavePix(
                clienteId = RemoveChavePixEndpointTest.CLIENTE_ID,
                tipoChavePix = TipoChavePix.CPF,
                chavePix = "01443493023",
                tipoConta = TipoConta.CONTA_CORRENTE,
            ))
    }

    @AfterEach
    fun cleanUp(){
        repositorio.deleteAll()
    }

    @Test
    fun `deve remover uma chave pix ja cadastrada`(){

        `when`(bcbClient.removerChavePixDoBCB(chavePix = "01443493023", obterRequestRemoverChavePixBCB("01443493023")))
            .thenReturn(HttpResponse.ok(obterResponseRemoverChavePixBCB()))

        val response = grpc.removerChavePix(RemoverChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setPixId(OBJETO_SALVO.id.toString())
                .build())

        assertEquals(OBJETO_SALVO.id.toString(), response.pixId)
        assertEquals(OBJETO_SALVO.clienteId.toString(), response.clienteId)
    }

    @Test
    fun `nao deve remover chave pix quando chave inexistente`(){
        val pixIdNaoExistente = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpc.removerChavePix(RemoverChavePixRequest.newBuilder()
                .setPixId(pixIdNaoExistente)
                .setClienteId(OBJETO_SALVO.clienteId.toString())
                .build())
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não existe.", status.description)
        }
    }

    @Test
    fun `nao deve remover chave existente mas pertecente outro cliente`(){
        val outroClienteId = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpc.removerChavePix(RemoverChavePixRequest.newBuilder()
                .setPixId(OBJETO_SALVO.id.toString())
                .setClienteId(outroClienteId)
                .build())
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não existe.", status.description)
        }
    }

    @Test
    fun `nao deve remover chave pix existente quando ocorrer erro na integracao BCB`(){
        `when`(bcbClient.removerChavePixDoBCB(chavePix = "01443493023", obterRequestRemoverChavePixBCB("01443493023")))
            .thenReturn(HttpResponse.unprocessableEntity())

        val thrown = assertThrows<StatusRuntimeException> {
            grpc.removerChavePix(RemoverChavePixRequest.newBuilder()
                .setPixId(OBJETO_SALVO.id.toString())
                .setClienteId((OBJETO_SALVO.clienteId.toString()))
                .build())
        }

        with(thrown){
            assertEquals((Status.INVALID_ARGUMENT).code, status.code)
            assertEquals("Erro ao remover chave Pix no Banco Central do Brasil (BCB)", status.description)
        }
    }

    fun obterRequestRemoverChavePixBCB(chavePix: String): RemoverChavePixDoBCBRequest{
        return RemoverChavePixDoBCBRequest(chavePix = chavePix)
    }

    fun obterResponseRemoverChavePixBCB(): RemoverChavePixDoBCBResponse{
        return RemoverChavePixDoBCBResponse(
            chavePix = "01443493023",
            donoChave = "Izabel Silva",
            deletadoEm = LocalDateTime.now()
        )
    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub?{
            return GerenciadorChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}