package br.com.zup.ot6.izabel.dominio.servicos.impl

import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO
import br.com.zup.ot6.izabel.aplicacao.excecoes.ChaveExistenteExcecao
import br.com.zup.ot6.izabel.aplicacao.excecoes.ClienteNaoEncontradoExcecao
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
    @Inject val integracaoERP: IntegracaoERP ): ChavePixService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun cadastrarChavePix(@Valid chavePixDTO: CadastrarChavePixDTO): ChavePix {
        logger.info("Inicio do cadastro de chave Pix.")

        logger.info("Verificando se a chave Pix ${chavePixDTO.chavePix} já existe.")
        if (repositorioChavePix.existsByChavePix(chavePixDTO.chavePix)) throw ChaveExistenteExcecao("Chave Pix já existe.")

        val response = integracaoERP.buscaContasPorTipo(chavePixDTO.clienteId, chavePixDTO.tipoConta.name)

        if(response.status != HttpStatus.OK ){
            throw ClienteNaoEncontradoExcecao("Cliente não encontrado.")}

        logger.info("Registrando chave Pix.")
        val chavePix: ChavePix = chavePixDTO.converterParaEntidade()

        return repositorioChavePix.save(chavePix)

    }

    @Transactional
    override fun removerChavePix(clienteId: String, pixId: String) {
        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteID = UUID.fromString(clienteId)

        val chave = repositorioChavePix.findByIdAndClienteId(uuidPixId, uuidClienteID)
        if (!chave.isPresent ){throw ClienteNaoEncontradoExcecao("Cliente não existe.")}

        repositorioChavePix.deleteById(uuidPixId)

    }

}