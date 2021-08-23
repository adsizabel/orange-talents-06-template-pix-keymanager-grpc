package br.com.zup.ot6.izabel.dominio.servicos

import br.com.zup.ot6.izabel.TipoConta
import br.com.zup.ot6.izabel.aplicacao.dto.erp.DadosDaContaResponse
import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO
import br.com.zup.ot6.izabel.aplicacao.dto.pix.ChavePixInfoDTO
import br.com.zup.ot6.izabel.aplicacao.dto.pix.ConsultaChavePixDTO
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix

interface ChavePixService {

    fun cadastrarChavePix(cadastrarChavePixDTO: CadastrarChavePixDTO): ChavePix
    fun removerChavePix(clienteId: String, pixId: String)
    fun listarChavePix(clienteId: String): List<ChavePix>
    fun getDadosContaCliente(codigoCliente: String, tipoConta: TipoConta): DadosDaContaResponse
}
