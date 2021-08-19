package br.com.zup.ot6.izabel.aplicacao.excecoes

import java.lang.RuntimeException

class ChaveExistenteExcecao(val mensagem: String): RuntimeException(mensagem) {

}