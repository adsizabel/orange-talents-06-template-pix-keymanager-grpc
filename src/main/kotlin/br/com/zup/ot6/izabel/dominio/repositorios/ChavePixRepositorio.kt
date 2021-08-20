package br.com.zup.ot6.izabel.dominio.repositorios

import br.com.zup.ot6.izabel.dominio.entidades.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepositorio: JpaRepository<ChavePix, UUID> {

    fun existsByChavePix(chavePix: String?): Boolean
    fun findByIdAndClienteId(uuidPixId: UUID, uuidClienteID: UUID): Optional<ChavePix>
}