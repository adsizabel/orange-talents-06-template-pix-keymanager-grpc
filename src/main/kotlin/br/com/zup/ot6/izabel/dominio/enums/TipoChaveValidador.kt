package br.com.zup.ot6.izabel.dominio.enums

import br.com.zup.ot6.izabel.aplicacao.excecoes.CampoInvalidoExcecao

private const val TAMANHO_MAXIMO_CHAVE = 77
private const val MENSAGEM_DE_ERRO = "A chave do tipo %s deve respeitar o padrão: %s."

enum class TipoChaveValidador(val descricao: String) {
    CPF("CPF") {
        override fun isValida(chave: String): Boolean {
            if(isTamanhoValido(chave) && chave.matches("^[0-9]{11}\$".toRegex())) return true
            throw CampoInvalidoExcecao(MENSAGEM_DE_ERRO.format(descricao, "12345678901"))
        }
    },
    CHAVE_ALEATORIA("CHAVE_ALEATORIA") {
        override fun isValida(chave: String): Boolean {
            if(chave.isNullOrEmpty()) return true
            throw CampoInvalidoExcecao(MENSAGEM_DE_ERRO.format(CHAVE_ALEATORIA.descricao, "STRING VAZIA"))
        }
    },
    EMAIL("EMAIL") {
        override fun isValida(chave: String): Boolean {
            val pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            if (isTamanhoValido(chave) && chave.matches(pattern.toRegex())) return true
            throw CampoInvalidoExcecao(MENSAGEM_DE_ERRO.format(EMAIL.descricao, "exemplo@email.com"))
        }
    },
    TELEFONE("TELEFONE") {
        override fun isValida(chave: String): Boolean {
            if(isTamanhoValido(chave) && chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) return true
            throw CampoInvalidoExcecao(MENSAGEM_DE_ERRO.format(TELEFONE.descricao, "+5585988714077"))
        }
    };

    abstract fun isValida(chave: String): Boolean

    companion object {

        fun comDescricao(descricao: String): TipoChaveValidador{
            return TipoChaveValidador.values().first { it.descricao == descricao }
        }

        fun isTamanhoValido(chave: String): Boolean{
            if(chave.isNullOrBlank()) throw CampoInvalidoExcecao("O campo chave não pode ser nulo ou vazio.")
            if(chave.length > TAMANHO_MAXIMO_CHAVE)
                throw CampoInvalidoExcecao("Tamanho maximo de $TAMANHO_MAXIMO_CHAVE caracteres excedido para o campo chave.")
            return true
        }
    }

}
