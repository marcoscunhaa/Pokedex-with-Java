package br.com.marcoscunha.PokedexApi.service;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import br.com.marcoscunha.PokedexApi.repository.PokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PokemonServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PokemonRepository repository;

    @InjectMocks
    private PokemonService pokemonService;

    private Map<String, Object> mockApiResponse;
    private Map<String, Object> mockSpeciesData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criando a resposta mockada da API
        mockApiResponse = new HashMap<>();
        mockApiResponse.put("name", "palkia");
        mockApiResponse.put("height", 18);
        mockApiResponse.put("weight", 683);
        mockApiResponse.put("id", 484);

        List<Map<String, Object>> types = new ArrayList<>();
        Map<String, Object> type = new HashMap<>();
        type.put("type", Collections.singletonMap("name", "dragon"));
        types.add(type);
        mockApiResponse.put("types", types);

        List<Map<String, Object>> abilities = new ArrayList<>();
        Map<String, Object> ability = new HashMap<>();
        ability.put("ability", Collections.singletonMap("name", "pressure"));
        abilities.add(ability);
        mockApiResponse.put("abilities", abilities);

        List<Map<String, Object>> moves = new ArrayList<>();
        Map<String, Object> move = new HashMap<>();
        move.put("move", Collections.singletonMap("name", "spacial-rend"));
        moves.add(move);
        mockApiResponse.put("moves", moves);

        List<Map<String, Object>> stats = new ArrayList<>();
        Map<String, Object> stat = new HashMap<>();
        stat.put("stat", Collections.singletonMap("name", "hp"));
        stat.put("base_stat", 100);
        stats.add(stat);
        mockApiResponse.put("stats", stats);

        // Criando a resposta mockada da "species" (dados adicionais)
        mockSpeciesData = new HashMap<>();
        Map<String, Object> species = new HashMap<>();
        species.put("url", "http://mockurl.com");
        mockSpeciesData.put("species", species);
        mockSpeciesData.put("flavor_text_entries", Collections.singletonList(Collections.singletonMap("flavor_text", "Palkia controls space.")));
    }

    @Test
    void testFetchAndSavePokemon_Failure_ApiError() {
        String pokemonName = "palkia";

        // Mockando a resposta da API para retornar null
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        Pokemon result = pokemonService.fetchAndSavePokemon(pokemonName);

        assertNull(result);  // Verificando que o resultado é null
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));  // Verificando se a API foi chamada
        verify(repository, times(0)).save(any(Pokemon.class));  // Verificando se o Pokémon não foi salvo
    }

    @Test
    void testFetchAndSavePokemon_Failure_MissingName() {
        String pokemonName = "palkia";

        // Simulando uma resposta onde o nome está ausente
        Map<String, Object> mockApiResponseWithoutName = new HashMap<>(mockApiResponse);
        mockApiResponseWithoutName.put("name", null);  // Fazendo o nome ser null

        // Mockando a resposta da API
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockApiResponseWithoutName);

        Pokemon result = pokemonService.fetchAndSavePokemon(pokemonName);

        // O resultado deve ser null, pois o nome não foi encontrado
        assertNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
        verify(repository, times(0)).save(any(Pokemon.class));
    }

    @Test
    void testFetchAndSavePokemon_Failure_MissingSpeciesUrl() {
        String pokemonName = "palkia";

        // Simulando uma resposta onde o campo "species" ou "url" está ausente
        Map<String, Object> mockApiResponseWithoutSpeciesUrl = new HashMap<>(mockApiResponse);
        mockApiResponseWithoutSpeciesUrl.put("species", null);  // Fazendo o campo "species" ser null

        // Mockando a resposta da API
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockApiResponseWithoutSpeciesUrl);

        // Mockando o comportamento do repositório
        when(repository.save(any(Pokemon.class))).thenReturn(new Pokemon());

        Pokemon result = pokemonService.fetchAndSavePokemon(pokemonName);

        // O Pokémon ainda deve ser salvo mesmo que a URL de species esteja ausente, porque o fallback é acionado
        assertNotNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
        verify(repository, times(1)).save(any(Pokemon.class));
    }

}
