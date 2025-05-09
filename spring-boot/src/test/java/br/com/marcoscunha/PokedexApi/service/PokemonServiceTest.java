package br.com.marcoscunha.PokedexApi.service;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import br.com.marcoscunha.PokedexApi.repository.PokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PokemonServiceTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PokemonService pokemonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchAndSave() {
        String name = "dialga";

        // Mock da resposta da API de /pokemon/dialga
        Map<String, Object> fakePokemonResponse = new HashMap<>();
        fakePokemonResponse.put("name", name);
        fakePokemonResponse.put("height", 54);
        fakePokemonResponse.put("weight", 6830);
        fakePokemonResponse.put("types", List.of(
                Map.of("type", Map.of("name", "steel")),
                Map.of("type", Map.of("name", "dragon"))
        ));
        fakePokemonResponse.put("abilities", List.of(Map.of("ability", Map.of("name", "pressure"))));
        fakePokemonResponse.put("moves", List.of(Map.of("move", Map.of("name", "roar-of-time"))));
        fakePokemonResponse.put("stats", List.of(Map.of("stat", Map.of("name", "special-attack"), "base_stat", 150)));
        fakePokemonResponse.put("sprites", Map.of("front_default", "https://img.dialga.png"));
        fakePokemonResponse.put("species", Map.of("url", "https://pokeapi.co/api/v2/pokemon-species/483/"));

        // Mock da resposta da species
        Map<String, Object> speciesResponse = new HashMap<>();
        speciesResponse.put("varieties", List.of(Map.of("pokemon", Map.of("name", "dialga"))));
        speciesResponse.put("generation", Map.of("name", "generation-iv"));
        speciesResponse.put("evolution_chain", Map.of("url", "https://pokeapi.co/api/v2/evolution-chain/483/"));

        // Mock da resposta da evolução (Dialga não evolui)
        Map<String, Object> evolutionResponse = Map.of("chain", Map.of(
                "species", Map.of("name", "dialga"),
                "evolves_to", List.of()
        ));

        // Configura os retornos simulados
        when(restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/" + name, Map.class))
                .thenReturn(fakePokemonResponse);

        when(restTemplate.getForEntity("https://pokeapi.co/api/v2/pokemon-species/483/", Map.class))
                .thenReturn(ResponseEntity.ok(speciesResponse));

        when(restTemplate.getForEntity("https://pokeapi.co/api/v2/evolution-chain/483/", Map.class))
                .thenReturn(ResponseEntity.ok(evolutionResponse));

        when(pokemonRepository.save(any(Pokemon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Executa o método a ser testado
        pokemonService.fetchAndSavePokemon(name);

        // Verifica se o Pokémon foi salvo no repositório
        verify(pokemonRepository, times(1)).save(any(Pokemon.class));
    }

    @Test
    void testGetAllPokemons() {
        Pokemon dialga = new Pokemon();
        dialga.setName("Dialga");

        Pokemon palkia = new Pokemon();
        palkia.setName("Palkia");

        List<Pokemon> pokemons = List.of(dialga, palkia);

        when(pokemonRepository.findAll()).thenReturn(pokemons);

        List<Pokemon> result = pokemonService.getAllPokemons();

        assertEquals(2, result.size());
        assertEquals("Dialga", result.get(0).getName());
        assertEquals("Palkia", result.get(1).getName());
        verify(pokemonRepository, times(1)).findAll();
    }

    @Test
    void testFindByName() {
        Pokemon dialga = new Pokemon();
        dialga.setName("Dialga");
        List<Pokemon> pokemons = List.of(dialga);

        when(pokemonRepository.findByNameContainingIgnoreCase("dialga")).thenReturn(pokemons);

        List<Pokemon> result = pokemonService.findByName("dialga");

        assertEquals(1, result.size());
        assertEquals("Dialga", result.get(0).getName());
        verify(pokemonRepository, times(1)).findByNameContainingIgnoreCase("dialga");
    }

    @Test
    void testFindByType() {
        Pokemon dialga = new Pokemon();
        dialga.setName("Dialga");
        List<Pokemon> pokemons = List.of(dialga);

        when(pokemonRepository.findByTypeContainingIgnoreCase("steel")).thenReturn(pokemons);

        List<Pokemon> result = pokemonService.findByType("steel");

        assertEquals(1, result.size());
        assertEquals("Dialga", result.get(0).getName());
        verify(pokemonRepository, times(1)).findByTypeContainingIgnoreCase("steel");
    }

    @Test
    void testFindByAbility() {
        Pokemon dialga = new Pokemon();
        dialga.setName("Dialga");
        List<Pokemon> pokemons = List.of(dialga);

        when(pokemonRepository.findByAbilityContainingIgnoreCase("pressure")).thenReturn(pokemons);

        List<Pokemon> result = pokemonService.findByAbility("pressure");

        assertEquals(1, result.size());
        assertEquals("Dialga", result.get(0).getName());
        verify(pokemonRepository, times(1)).findByAbilityContainingIgnoreCase("pressure");
    }

    @Test
    void testFindByMove() {
        Pokemon dialga = new Pokemon();
        dialga.setName("Dialga");
        List<Pokemon> pokemons = List.of(dialga);

        when(pokemonRepository.findByMoveContainingIgnoreCase("roar-of-time")).thenReturn(pokemons);

        List<Pokemon> result = pokemonService.findByMove("roar-of-time");

        assertEquals(1, result.size());
        assertEquals("Dialga", result.get(0).getName());
        verify(pokemonRepository, times(1)).findByMoveContainingIgnoreCase("roar-of-time");
    }

    @Test
    void testFindByGeneration() {
        Pokemon dialga = new Pokemon();
        dialga.setName("Dialga");
        List<Pokemon> pokemons = List.of(dialga);

        when(pokemonRepository.findByGenerationContainingIgnoreCase("generation-iv")).thenReturn(pokemons);

        List<Pokemon> result = pokemonService.findByGeneration("generation-iv");

        assertEquals(1, result.size());
        assertEquals("Dialga", result.get(0).getName());
        verify(pokemonRepository, times(1)).findByGenerationContainingIgnoreCase("generation-iv");
    }
}
