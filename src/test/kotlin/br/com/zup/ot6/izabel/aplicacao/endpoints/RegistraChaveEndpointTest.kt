package br.com.zup.ot6.izabel.aplicacao.endpoints

import br.com.zup.ot6.izabel.CadastrarChavePixRequest
import br.com.zup.ot6.izabel.GerenciadorChavePixGrpcServiceGrpc
import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import br.com.zup.ot6.izabel.aplicacao.dto.erp.DadosDaContaResponse
import br.com.zup.ot6.izabel.aplicacao.dto.erp.InstituicaoResponse
import br.com.zup.ot6.izabel.aplicacao.dto.erp.TitularResponse
import br.com.zup.ot6.izabel.aplicacao.integracoes.IntegracaoERP
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val repositorio: ChavePixRepositorio,
    val grpcClient: GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: IntegracaoERP

    companion object{
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup(){
        repositorio.deleteAll()
    }

    @MockBean(IntegracaoERP::class)
    fun itauClient(): IntegracaoERP {
        return  Mockito.mock(IntegracaoERP::class.java)
    }

    @Test
    fun `deve cadastrar nova chave pix`() {

        // cenario
        `when`(itauClient.buscaContasPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(obterDadosContaClienteItauResponse()))

        //acao
        val response = grpcClient.cadastrarChavePix(
            CadastrarChavePixRequest.newBuilder()
            .setIdCliente(CLIENTE_ID.toString())
            .setTipoChavePix(TipoChavePix.EMAIL)
            .setChavePix("izabelsilva@gmail.com")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())

        //assertivas
        with(response){
            assertEquals(CLIENTE_ID.toString(), idCliente)
            assertNotNull(idChavePix)
        }

    }

    @Test
    fun `nao deve cadastrar chave pix ja existente`() {

        repositorio.save(ChavePix(
            clienteId = CLIENTE_ID,
            tipoChavePix = TipoChavePix.CPF,
            chavePix = "01443493023",
            tipoConta = TipoConta.CONTA_CORRENTE,
        ))

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(CadastrarChavePixRequest.newBuilder()
                .setIdCliente(CLIENTE_ID.toString())
                .setTipoChavePix(TipoChavePix.CPF)
                .setChavePix("01443493023")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix já existe.", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar chave pix quando nao encontar dados da conta do cliente`() {

        `when`(itauClient.buscaContasPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(CadastrarChavePixRequest.newBuilder()
                .setIdCliente(CLIENTE_ID.toString())
                .setTipoChavePix(TipoChavePix.CPF)
                .setChavePix("01443493023")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não encontrado.", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar chave pix quando parametros forem invalidos`() {
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(CadastrarChavePixRequest.newBuilder().build())
        }

        with(thrown){
            assertEquals(Status.INTERNAL.code, status.code)
            //assertEquals("Dad")
        }
    }

   fun obterDadosContaClienteItauResponse(): DadosDaContaResponse{
       return DadosDaContaResponse(
           tipo = "CONTA_CORRENTE",
           instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190") ,
           agencia = "0001",
           numero = "291900",
           titular = TitularResponse(id = CLIENTE_ID , nome = "Izabel Silva", cpf = "50904669041"),
       )
   }

    @Factory
    class Clients{

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceBlockingStub? {
            return GerenciadorChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}