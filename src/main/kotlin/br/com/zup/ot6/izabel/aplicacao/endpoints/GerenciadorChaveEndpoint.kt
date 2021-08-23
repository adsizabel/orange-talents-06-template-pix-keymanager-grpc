package br.com.zup.ot6.izabel.aplicacao.endpoints

import br.com.zup.ot6.izabel.*
import br.com.zup.ot6.izabel.aplicacao.excecoes.ChaveExistenteExcecao
import br.com.zup.ot6.izabel.aplicacao.excecoes.ClienteNaoEncontradoExcecao
import br.com.zup.ot6.izabel.aplicacao.extensoes.CarregaChavePixReponseConverter
import br.com.zup.ot6.izabel.aplicacao.extensoes.fromGoogleTimestamp
import br.com.zup.ot6.izabel.aplicacao.extensoes.paraDTO
import br.com.zup.ot6.izabel.aplicacao.integracoes.IntegracaoBCB
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import br.com.zup.ot6.izabel.dominio.repositorios.ChavePixRepositorio
import br.com.zup.ot6.izabel.dominio.servicos.ChavePixService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GerenciadorChaveEndpoint(
    @Inject private val chavePixService: ChavePixService,
    @Inject private val repositorio: ChavePixRepositorio,
    @Inject private val bcbCliente: IntegracaoBCB
): GerenciadorChavePixGrpcServiceGrpc.GerenciadorChavePixGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

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

    override fun carregarChavePix(
        request: CarregaChavePixRequest,
        responseObserver: StreamObserver<CarregaChavePixResponse>
    ) {

        val filtro = request.paraDTO()
        val chaveInfo = filtro.filtra(repositorio = repositorio, bcbCliente = bcbCliente, service = chavePixService)

        responseObserver.onNext(CarregaChavePixReponseConverter().converter(chaveInfo))
        responseObserver.onCompleted()
    }

    override fun listarChavePix(
        request: ListarChavePixRequest,
        responseObserver: StreamObserver<ListarChavePixResponse>
    ) {
        try {
            if(request.clienteId.isNullOrBlank())
                throw IllegalArgumentException("Campo clienteId não pode ser nulo ou vazio.")

            val builder = ListarChavePixResponse.newBuilder()

            logger.info("Consultando todos as chaves pix para o cliente ${request.clienteId}")
            chavePixService.listarChavePix(request.clienteId).forEach() { chave ->
                //Interagindo na lista retornada do banco e criando novos ChavePixResponse
                //E adicionando no builder, que será o "corpo do nosso response"
                builder.addChavesPix(mapParaChavePixResponse(chave))
            }

            responseObserver.onNext(builder.build())
            responseObserver.onCompleted()
        } catch (e: Throwable){
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

    private fun mapParaChavePixResponse(chave: ChavePix) = ChavePixResponse.newBuilder()
        .setPixId(chave.id.toString())
        .setClienteId(chave.clienteId.toString())
        .setTipoChave(chave.tipoChavePix)
        .setValorChave(chave.chavePix)
        .setTipoConta(chave.tipoConta)
        .setDataCriacao(chave.criadoEm.fromGoogleTimestamp())

        .build()

}