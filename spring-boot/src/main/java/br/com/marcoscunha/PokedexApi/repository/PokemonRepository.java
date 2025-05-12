package br.com.marcoscunha.PokedexApi.repository;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findAllByOrderByIdAsc();

    List<Pokemon> findByNameContainingIgnoreCaseOrderByIdAsc(String name);

    List<Pokemon> findByTypeContainingIgnoreCaseOrderByIdAsc(String type);

    List<Pokemon> findByAbilityContainingIgnoreCaseOrderByIdAsc(String ability);

    List<Pokemon> findByMoveContainingIgnoreCaseOrderByIdAsc(String move);

    List<Pokemon> findByGenerationContainingIgnoreCaseOrderByIdAsc(String generation);

    Page<Pokemon> findAll(Pageable pageable);
}
