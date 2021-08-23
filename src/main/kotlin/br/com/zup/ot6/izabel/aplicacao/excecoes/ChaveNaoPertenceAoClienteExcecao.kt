package br.com.zup.ot6.izabel.aplicacao.excecoes

import java.lang.RuntimeException

class ChaveNaoPertenceAoClienteExcecao(val mensagem: String): RuntimeException(mensagem) {
}