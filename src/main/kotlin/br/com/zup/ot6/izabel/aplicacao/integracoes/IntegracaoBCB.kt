package br.com.zup.ot6.izabel.aplicacao.integracoes

import br.com.zup.ot6.izabel.aplicacao.dto.bcb.*
import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "\${bcb.pix.url}")
interface IntegracaoBCB {

    @Get(
        value = "/api/v1/pix/keys/{chavePix}",
        consumes = [MediaType.APPLICATION_XML]
    )
    fun pesquisarChavePix(@PathVariable chavePix: String) : HttpResponse<DetalhesChavePixResponse>

    @Post(
        value ="/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun cadastrarChaveNoBCB(@Body request: CadastrarChavePixNoBCBRequest): HttpResponse<CadastrarChavePixNoBCBResponse>

    @Delete(
        value = "/api/v1/pix/keys/{chavePix}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun removerChavePixDoBCB(@PathVariable chavePix: String, @Body request: RemoverChavePixDoBCBRequest): HttpResponse<RemoverChavePixDoBCBResponse>
}