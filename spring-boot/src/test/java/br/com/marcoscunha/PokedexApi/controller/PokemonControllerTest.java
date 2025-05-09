package br.com.marcoscunha.PokedexApi.controller;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import br.com.marcoscunha.PokedexApi.service.PokemonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Adiciona o suporte ao Mockito
@ExtendWith(MockitoExtension.class)
public class PokemonControllerTest {

    // Mock do serviço
    private MockMvc mockMvc;

    @Mock
    private PokemonService service;

    // Injeção do controller com mocks
    @InjectMocks
    private PokemonController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Pokemon samplePokemon() {
        return new Pokemon(
                483,
                "dialga",
                "Dialga controla o tempo, sendo uma das criaturas mais poderosas do universo Pokémon.",
                18,
                683,
                "sprite_url",
                Arrays.asList("steel", "dragon"),
                Arrays.asList("pressure"),
                Arrays.asList("roar of time"),
                Map.of("hp", 100),
                Arrays.asList("palkia")
        );
    }

    // Configuração do MockMvc
    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // Chama o setup para inicializar o MockMvc
    @Test
    void testGetAllPokemons() throws Exception {
        setup();

        List<Pokemon> pokemons = Arrays.asList(samplePokemon());

        // Quando o serviço for chamado, retorna o Pokémon mockado
        Mockito.when(service.getAllPokemons()).thenReturn(pokemons);

        mockMvc.perform(get("/api/pokemons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("dialga"));
    }

    @Test
    void testGetByIdFound() throws Exception {
        setup();

        // Mocka o serviço para retornar o Pokémon diretamente quando o ID 483 for solicitado
        Mockito.when(service.findById(483L)).thenReturn(samplePokemon());

        mockMvc.perform(get("/api/pokemons/483"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("dialga"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        setup();

        // Mocka o serviço para retornar null quando o ID não for encontrado
        Mockito.when(service.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/pokemons/999"))
                .andExpect(status().isNotFound());
    }




    @Test
    void testGetByNameFound() throws Exception {
        setup();

        Mockito.when(service.findByName("dialga")).thenReturn(List.of(samplePokemon()));

        mockMvc.perform(get("/api/pokemons/name").param("name", "dialga"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("dialga"));
    }

    @Test
    void testGetByNameNotFound() throws Exception {
        setup();

        Mockito.when(service.findByName("missingno")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pokemons/name").param("name", "missingno"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetByTypeFound() throws Exception {
        setup();

        Mockito.when(service.findByType("steel")).thenReturn(List.of(samplePokemon()));

        mockMvc.perform(get("/api/pokemons/type").param("type", "steel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type[0]").value("steel"));
    }

    @Test
    void testGetByAbilityFound() throws Exception {
        setup();

        Mockito.when(service.findByAbility("pressure")).thenReturn(List.of(samplePokemon()));

        mockMvc.perform(get("/api/pokemons/ability").param("ability", "pressure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ability[0]").value("pressure"));
    }

    @Test
    void testGetByMoveFound() throws Exception {
        setup();

        Mockito.when(service.findByMove("roar of time")).thenReturn(List.of(samplePokemon()));

        mockMvc.perform(get("/api/pokemons/move").param("move", "roar of time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].move[0]").value("roar of time"));
    }

    @Test
    void testImportAll() throws Exception {
        setup();

        mockMvc.perform(post("/api/pokemons/import"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Todos os Pokémons foram importados com sucesso!!"));
    }
}
