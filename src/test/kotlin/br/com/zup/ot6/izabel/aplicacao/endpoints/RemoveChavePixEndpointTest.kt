package br.com.zup.ot6.izabel.aplicacao.endpoints

import br.com.zup.ot6.izabel.*
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import br.com.zup.ot6.izabel.dominio.repositorios.ChavePixRepositorio
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChavePixEndpointTest(
    val repositorio: ChavePixRepositorio,
    val grpc: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub
    ) {

    lateinit var OBJETO_SALVO: ChavePix

    companion object{
        val CLIENTE_ID = UUID.randomUUID()
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

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub?{
            return GerenciadorChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}