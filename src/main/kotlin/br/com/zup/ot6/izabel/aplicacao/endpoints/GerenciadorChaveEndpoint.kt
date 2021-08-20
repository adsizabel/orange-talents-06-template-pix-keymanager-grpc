package br.com.zup.ot6.izabel.aplicacao.endpoints

import br.com.zup.ot6.izabel.*
import br.com.zup.ot6.izabel.aplicacao.excecoes.ChaveExistenteExcecao
import br.com.zup.ot6.izabel.aplicacao.excecoes.ClienteNaoEncontradoExcecao
import br.com.zup.ot6.izabel.aplicacao.extensoes.paraDTO
import br.com.zup.ot6.izabel.dominio.servicos.ChavePixService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
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
                    .setIdChavePix(chaveCriada.id.toString())
                    .setIdCliente(chaveCriada.clienteId.toString())
                    .build()
            )
            responseObserver.onCompleted()

        } catch (e: Throwable) {
            responseObserver.onError(tratarErros(e))
        }
    }

    override fun removerChavePix(
        request: RemoverChavePixRequest,
        responseObserver: StreamObserver<RemoverChavePixResponse>
    ) {
        try {
            chavePixService.removerChavePix(clienteId = request.clienteId, pixId = request.pixId)

            responseObserver.onNext(
                RemoverChavePixResponse.newBuilder()
                    .setClienteId(request.clienteId)
                    .setPixId(request.pixId)
                    .build()
            )
            responseObserver.onCompleted()
        } catch (e: Throwable) {
            responseObserver.onError(tratarErros(e))
    }
}
    private fun tratarErros(excecao: Throwable): StatusRuntimeException {
        return when(excecao){
            is ClienteNaoEncontradoExcecao -> obterStatusRuntimeException(Status.NOT_FOUND, excecao)
            is ChaveExistenteExcecao -> obterStatusRuntimeException(Status.ALREADY_EXISTS, excecao)
            is IllegalArgumentException -> obterStatusRuntimeException(Status.INVALID_ARGUMENT, excecao)
            else -> obterStatusRuntimeException(Status.INTERNAL, excecao)
        }
    }

    private fun obterStatusRuntimeException(status: Status, excecao: Throwable): StatusRuntimeException {
        return status.withDescription(excecao.message).withCause(excecao.cause).asRuntimeException()
    }
}