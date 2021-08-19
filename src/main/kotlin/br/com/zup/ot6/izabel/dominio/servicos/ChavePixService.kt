package br.com.zup.ot6.izabel.dominio.servicos

import br.com.zup.ot6.izabel.aplicacao.dto.pix.CadastrarChavePixDTO
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix

interface ChavePixService {

    fun cadastrarChavePix(cadastrarChavePixDTO: CadastrarChavePixDTO): ChavePix

}
