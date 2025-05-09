package br.com.marcoscunha.PokedexApi.repository;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findByNameContainingIgnoreCase(String name);
    List<Pokemon> findByTypeContainingIgnoreCase(String type);
    List<Pokemon> findByAbilityContainingIgnoreCase(String ability);
    List<Pokemon> findByMoveContainingIgnoreCase(String move);
    List<Pokemon> findByGenerationContainingIgnoreCase(String generation);
}
