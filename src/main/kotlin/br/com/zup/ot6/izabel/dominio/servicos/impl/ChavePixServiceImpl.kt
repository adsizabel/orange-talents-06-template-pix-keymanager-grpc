package br.com.zup.ot6.izabel.dominio.servicos.impl

import br.com.zup.ot6.izabel.TipoConta
import br.com.zup.ot6.izabel.aplicacao.dto.bcb.CadastrarChavePixNoBCBRequest
import br.com.zup.ot6.izabel.aplicacao.dto.bcb.RemoverChavePixDoBCBRequest
import br.com.zup.ot6.izabel.aplicacao.dto.erp.DadosDaContaResponse
import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO
import br.com.zup.ot6.izabel.aplicacao.excecoes.ChaveExistenteExcecao
import br.com.zup.ot6.izabel.aplicacao.excecoes.ClienteNaoEncontradoExcecao
import br.com.zup.ot6.izabel.aplicacao.integracoes.IntegracaoBCB
import br.com.zup.ot6.izabel.aplicacao.integracoes.IntegracaoERP
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import br.com.zup.ot6.izabel.dominio.repositorios.ChavePixRepositorio
import br.com.zup.ot6.izabel.dominio.servicos.ChavePixService
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixServiceImpl(
    @Inject val repositorioChavePix: ChavePixRepositorio,
    @Inject val integracaoERP: IntegracaoERP,
    @Inject val integracaoBCB: IntegracaoBCB): ChavePixService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun cadastrarChavePix(@Valid chavePixDTO: CadastrarChavePixDTO): ChavePix {
        logger.info("Inicio do cadastro de chave Pix.")

        logger.info("Verificando se a chave Pix ${chavePixDTO.chavePix} já existe.")
        if (repositorioChavePix.existsByChavePix(chavePixDTO.chavePix)) throw ChaveExistenteExcecao("Chave Pix já existe.")

        val responseDadosDaConta = getDadosContaCliente(chavePixDTO.clienteId, chavePixDTO.tipoConta)

        logger.info("Registrando chave Pix.")
        val chavePix: ChavePix = chavePixDTO.converterParaEntidade()
        repositorioChavePix.save(chavePix)

        val bcbRequest = CadastrarChavePixNoBCBRequest.criarChave(chavePixDTO, responseDadosDaConta)

        logger.info("Cadastrando chave Pix no Banco Central do Brasil - BCB")
        val bcbResponse = integracaoBCB.cadastrarChaveNoBCB(bcbRequest)

        if (bcbResponse.status != HttpStatus.CREATED) throw  IllegalArgumentException("Erro ao registrar chave Pix no Banco Central")

        logger.info("Cadastro com sucesso de chave Pix no Banco Central do Brasil - BCB")
        return repositorioChavePix.update(chavePix)
    }

    @Transactional
    override fun removerChavePix(clienteId: String, pixId: String) {
        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteID = UUID.fromString(clienteId)

        logger.info("Início da remoção da chave Pix.")

        val chave = repositorioChavePix.findByIdAndClienteId(uuidPixId, uuidClienteID)
        if (!chave.isPresent ){throw ClienteNaoEncontradoExcecao("Não foi possivel encontrar um cliente valido para o ID ${uuidClienteID}.")}

        repositorioChavePix.deleteById(uuidPixId)

        val bcbRequest = RemoverChavePixDoBCBRequest(chavePix = chave.get().chavePix)

        logger.info("Aguardando remoção da chave Pix - Banco Central do Brasil BCB.")
        val bcbResponse = integracaoBCB.removerChavePixDoBCB(chave.get().chavePix, bcbRequest)
        if (bcbResponse.status != HttpStatus.OK){ throw IllegalArgumentException("Erro ao remover chave Pix no Banco Central do Brasil (BCB)")}

        logger.info("Chave Pix removida com sucesso.")
    }

    override fun listarChavePix(clienteId: String): List<ChavePix> {
        return repositorioChavePix.findAllByClienteId(UUID.fromString(clienteId))
    }

    override fun getDadosContaCliente(codigoCliente: String, tipoConta: TipoConta): DadosDaContaResponse {
        logger.info("Consultando conta do cliente: $codigoCliente no ERP Itau.")
        val erpResponse = integracaoERP.buscaContasPorTipo(codigoCliente, tipoConta.name)

        if(erpResponse.status != HttpStatus.OK) {
            throw ClienteNaoEncontradoExcecao("Não foi possivel encontrar um cliente valido " +
                    "para o ID $codigoCliente.")
        }
        return erpResponse.body()
    }

}