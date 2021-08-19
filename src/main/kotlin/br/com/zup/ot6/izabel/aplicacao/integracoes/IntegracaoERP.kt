package br.com.zup.ot6.izabel.aplicacao.integracoes

import br.com.zup.ot6.izabel.aplicacao.dto.erp.DadosDaContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${erp.itau.url}")
interface IntegracaoERP {

    @Get(value = "/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaContasPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosDaContaResponse>

}