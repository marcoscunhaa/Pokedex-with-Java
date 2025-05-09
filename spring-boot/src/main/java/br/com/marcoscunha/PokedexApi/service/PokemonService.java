package br.com.marcoscunha.PokedexApi.service;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import br.com.marcoscunha.PokedexApi.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PokemonService {

    @Autowired
    private PokemonRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    public Pokemon fetchAndSavePokemon(String pokemonName) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonName.toLowerCase();
        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            System.err.printf("Erro ao buscar dados do Pokémon %s: %s%n", pokemonName, e.getMessage());
            return null;
        }

        if (response == null) return null;

        Pokemon pokemon = new Pokemon();
        pokemon.setName((String) response.get("name"));
        pokemon.setHeight((int) response.get("height"));
        pokemon.setWeight((int) response.get("weight"));

        // Types
        List<Map<String, Object>> types = (List<Map<String, Object>>) response.get("types");
        List<String> typeNames = new ArrayList<>();
        if (types != null) {
            for (Map<String, Object> typeInfo : types) {
                Map<String, String> type = (Map<String, String>) typeInfo.get("type");
                if (type != null && type.get("name") != null) {
                    typeNames.add(type.get("name"));
                }
            }
        }
        pokemon.setType(typeNames);

        // Abilities
        List<Map<String, Object>> abilities = (List<Map<String, Object>>) response.get("abilities");
        List<String> abilityNames = new ArrayList<>();
        if (abilities != null) {
            for (Map<String, Object> abilityInfo : abilities) {
                Map<String, String> ability = (Map<String, String>) abilityInfo.get("ability");
                if (ability != null && ability.get("name") != null) {
                    abilityNames.add(ability.get("name"));
                }
            }
        }
        pokemon.setAbility(abilityNames);

        // Moves
        List<Map<String, Object>> moves = (List<Map<String, Object>>) response.get("moves");
        List<String> moveNames = new ArrayList<>();
        if (moves != null) {
            for (Map<String, Object> moveInfo : moves) {
                Map<String, String> move = (Map<String, String>) moveInfo.get("move");
                if (move != null && move.get("name") != null) {
                    moveNames.add(move.get("name"));
                }
            }
        }
        pokemon.setMove(moveNames);

        // Stats
        List<Map<String, Object>> stats = (List<Map<String, Object>>) response.get("stats");
        Map<String, Integer> statMap = new HashMap<>();
        if (stats != null) {
            for (Map<String, Object> statInfo : stats) {
                Map<String, String> stat = (Map<String, String>) statInfo.get("stat");
                if (stat != null && stat.get("name") != null && statInfo.get("base_stat") instanceof Integer baseStat) {
                    statMap.put(stat.get("name"), baseStat);
                }
            }
        }
        pokemon.setStats(statMap);

        // Sprites
        Map<String, Object> sprites = (Map<String, Object>) response.get("sprites");
        List<String> imgUrls = new ArrayList<>();
        if (sprites != null) {
            for (Object value : sprites.values()) {
                if (value instanceof String spriteUrl && spriteUrl.endsWith(".png")) {
                    imgUrls.add(spriteUrl);
                }
            }
        }
        pokemon.setImgUrl(imgUrls);

        // Pega species URL da resposta principal
        Map<String, Object> speciesInfo = (Map<String, Object>) response.get("species");
        if (speciesInfo == null || speciesInfo.get("url") == null) {
            System.err.printf("Espécie nula para Pokémon %s%n", pokemonName);
            pokemon.setShape(List.of("unknown"));
            pokemon.setGeneration(List.of("unknown"));
            pokemon.setEvolution(List.of("unknown"));
            pokemon.setDescription("Descrição não encontrada.");
            return repository.save(pokemon);
        }

        String speciesUrl = (String) speciesInfo.get("url");

        // Chamada à species API
        ResponseEntity<Map> speciesResponse;
        Map<String, Object> speciesData;
        try {
            speciesResponse = restTemplate.getForEntity(speciesUrl, Map.class);
            speciesData = speciesResponse.getBody();
        } catch (RestClientException e) {
            System.err.printf("Erro ao buscar species de %s: %s%n", pokemonName, e.getMessage());
            pokemon.setShape(List.of("unknown"));
            pokemon.setGeneration(List.of("unknown"));
            pokemon.setEvolution(List.of("unknown"));
            pokemon.setDescription("Descrição não encontrada.");
            return repository.save(pokemon);
        }

        // Formas alternativas
        List<Map<String, Object>> varieties = (List<Map<String, Object>>) speciesData.get("varieties");
        List<String> forms = new ArrayList<>();
        if (varieties != null) {
            for (Map<String, Object> variety : varieties) {
                Map<String, String> varietyPokemon = (Map<String, String>) variety.get("pokemon");
                if (varietyPokemon != null && varietyPokemon.get("name") != null) {
                    forms.add(varietyPokemon.get("name"));
                }
            }
        }
        pokemon.setShape(forms.isEmpty() ? List.of("unknown") : forms);

        // Generation
        Map<String, String> generation = (Map<String, String>) speciesData.get("generation");
        if (generation != null && generation.get("name") != null) {
            pokemon.setGeneration(List.of(generation.get("name")));
        } else {
            pokemon.setGeneration(List.of("unknown"));
        }

        // Evolution
        Map<String, String> evolutionChain = (Map<String, String>) speciesData.get("evolution_chain");
        if (evolutionChain != null && evolutionChain.get("url") != null) {
            try {
                ResponseEntity<Map> evolutionResponse = restTemplate.getForEntity(evolutionChain.get("url"), Map.class);
                Map<String, Object> evolutionData = evolutionResponse.getBody();

                List<String> evolutionList = new ArrayList<>();
                if (evolutionData != null && evolutionData.get("chain") != null) {
                    Map<String, Object> chain = (Map<String, Object>) evolutionData.get("chain");
                    extractEvolutionChain(chain, evolutionList);
                }

                pokemon.setEvolution(evolutionList.isEmpty() ? List.of("unknown") : evolutionList);
            } catch (RestClientException e) {
                System.err.printf("Erro ao buscar cadeia evolutiva de %s: %s%n", pokemonName, e.getMessage());
                pokemon.setEvolution(List.of("unknown"));
            }
        } else {
            pokemon.setEvolution(List.of("unknown"));
        }

        // Descrição
        List<Map<String, Object>> flavorTextEntries = (List<Map<String, Object>>) speciesData.get("flavor_text_entries");
        String description = "Descrição não encontrada.";
        if (flavorTextEntries != null) {
            for (Map<String, Object> entry : flavorTextEntries) {
                Map<String, String> language = (Map<String, String>) entry.get("language");
                if (language != null && "en".equals(language.get("name"))) {
                    String rawText = (String) entry.get("flavor_text");
                    if (rawText != null && !rawText.isEmpty()) {
                        description = rawText.replaceAll("[\\n\\f]", " ").trim();
                        break;
                    }
                }
            }
        }
        pokemon.setDescription(description);

        return repository.save(pokemon);
    }

    public List<Pokemon> getAllPokemons() {
        return repository.findAll();
    }

    @SuppressWarnings("unchecked")
    private void extractEvolutionChain(Map<String, Object> node, List<String> evolutionList) {
        Map<String, String> species = (Map<String, String>) node.get("species");
        if (species != null && species.get("name") != null) {
            evolutionList.add(species.get("name"));
        }

        List<Map<String, Object>> evolvesTo = (List<Map<String, Object>>) node.get("evolves_to");
        if (evolvesTo != null) {
            for (Map<String, Object> next : evolvesTo) {
                extractEvolutionChain(next, evolutionList);
            }
        }
    }

    // Método para salvar todos os pokémons
    public void fetchAndSaveAllPokemons() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0";
        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            System.out.println("Erro ao buscar lista de pokémons: " + e.getMessage());
            return;
        }

        if (response == null || !response.containsKey("results")) {
            System.out.println("Resposta inválida da API.");
            return;
        }

        List<Map<String, String>> results = (List<Map<String, String>>) response.get("results");

        int count = 1;
        for (Map<String, String> pokemonData : results) {
            String name = pokemonData.get("name");
            try {
                System.out.printf("(%d/%d) Salvando: %s...%n", count, results.size(), name);
                fetchAndSavePokemon(name);
                count++;
            } catch (Exception e) {
                System.err.printf("Erro ao salvar Pokémon %s: %s%n", name, e.getMessage());
            }
        }

        System.out.println("Importação de todos os pokémons concluída.");
    }


    //Métodos de busca
    public List<Pokemon> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public List<Pokemon> findByType(String type) {
        return repository.findByTypeContainingIgnoreCase(type);
    }

    public List<Pokemon> findByAbility(String ability) {
        return repository.findByAbilityContainingIgnoreCase(ability);
    }

    public List<Pokemon> findByMove(String move) {
        return repository.findByMoveContainingIgnoreCase(move);
    }

    public List<Pokemon> findByGeneration(String generation) {
        return repository.findByGenerationContainingIgnoreCase(generation);
    }

}
