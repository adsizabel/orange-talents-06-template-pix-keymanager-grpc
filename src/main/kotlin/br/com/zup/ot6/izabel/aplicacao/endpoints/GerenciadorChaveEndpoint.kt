package br.com.zup.ot6.izabel.aplicacao.endpoints

import br.com.zup.ot6.izabel.CadastrarChavePixRequest
import br.com.zup.ot6.izabel.CadastrarChavePixResponse
import br.com.zup.ot6.izabel.GerenciadorChavePixGrpcServiceGrpc
import br.com.zup.ot6.izabel.aplicacao.excecoes.CampoInvalidoExcecao
import br.com.zup.ot6.izabel.aplicacao.excecoes.ChaveExistenteExcecao
import br.com.zup.ot6.izabel.aplicacao.excecoes.ClienteNaoEncontradoExcecao
import br.com.zup.ot6.izabel.aplicacao.extensoes.paraDTO
import br.com.zup.ot6.izabel.dominio.servicos.ChavePixService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.lang.Exception
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class GerenciadorChaveEndpoint(@Inject val chavePixService: ChavePixService): GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceImplBase() {

    override fun cadastrarChavePix(
        request: CadastrarChavePixRequest,
        responseObserver: StreamObserver<CadastrarChavePixResponse>
    ) {

        try {
            val novaChavePix = request.paraDTO()
            val chaveCriada = chavePixService.cadastrarChavePix(novaChavePix)

            responseObserver.onNext(
                CadastrarChavePixResponse.newBuilder()
                    .setIdChavePix(chaveCriada.chavePix.toString())
                    .setIdCliente(chaveCriada.clienteId.toString())
                    .build()
            )
            responseObserver.onCompleted()

        } catch (e: CampoInvalidoExcecao) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        } catch (e: ClienteNaoEncontradoExcecao) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        } catch (e: ChaveExistenteExcecao) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        } catch (e: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        } catch (e: Throwable) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )
        }
    }
}