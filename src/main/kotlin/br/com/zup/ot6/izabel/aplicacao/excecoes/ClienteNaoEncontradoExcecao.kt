package br.com.zup.ot6.izabel.aplicacao.excecoes

import java.lang.RuntimeException

class ClienteNaoEncontradoExcecao(mensagem: String): RuntimeException(mensagem) {
}