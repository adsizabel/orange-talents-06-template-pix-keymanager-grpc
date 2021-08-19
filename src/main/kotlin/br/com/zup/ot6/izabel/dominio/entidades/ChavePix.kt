package br.com.zup.ot6.izabel.dominio.entidades

import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Table(name = "chave_pix")
@Entity
data class ChavePix(
    @Column(nullable = false)
    @field:NotBlank
    val clienteId: UUID,

    @Column(nullable = false)
    @field:NotBlank
    @Enumerated(EnumType.STRING)
    val tipoChavePix: TipoChavePix,

    @Column(nullable = false, unique = true, length = 77)
    @field:NotBlank
    val chavePix: String,

    @Column(nullable = false)
    @field:NotBlank
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoConta
    ){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "UUID" )
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "UUID", nullable = false, updatable = false)
    @field:NotBlank
    val id: UUID? = null

    @Column(nullable = false)
    @field:NotBlank
    val criadoEm: LocalDateTime = LocalDateTime.now()

}
