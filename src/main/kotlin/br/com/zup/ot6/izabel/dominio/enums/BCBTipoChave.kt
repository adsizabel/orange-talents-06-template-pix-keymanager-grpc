package br.com.zup.ot6.izabel.dominio.enums

enum class BCBTipoChave(val descricao: String) {
    CPF(TipoChaveValidador.CPF.name),
    RANDOM(TipoChaveValidador.CHAVE_ALEATORIA.name),
    EMAIL(TipoChaveValidador.EMAIL.name),
    PHONE(TipoChaveValidador.TELEFONE.name);

    companion object{
        fun comDescricao(descricao: String): BCBTipoChave{
            return BCBTipoChave.values().first{
                it.descricao == descricao
            }
        }
    }
}