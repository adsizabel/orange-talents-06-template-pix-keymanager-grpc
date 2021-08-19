package br.com.zup.ot6.izabel.aplicacao.dto.pix

import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import br.com.zup.ot6.izabel.dominio.enums.TipoChaveValidador
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class CadastrarChavePixDTO(
    @field:NotBlank
    val clienteId: String,

    @field:NotBlank
    val tipoChavePix: TipoChavePix,

    @field:NotBlank
    @field:Size(max = 77)
    val chavePix: String,

    @field:NotBlank
    val tipoConta: TipoConta
) {

    init {
        TipoChaveValidador.comDescricao(tipoChavePix.name).isValida(chavePix)
    }

    fun converterParaEntidade(): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipoChavePix = this.tipoChavePix,
            chavePix = if (this.tipoChavePix == TipoChavePix.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.chavePix,
            tipoConta = this.tipoConta)
    }
}
