package br.com.zup.ot6.izabel.aplicacao.excecoes

import java.lang.RuntimeException

class CampoInvalidoExcecao(mensagem: String): RuntimeException(mensagem) {
}