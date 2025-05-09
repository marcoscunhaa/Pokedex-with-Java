package br.com.marcoscunha.PokedexApi.controller;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import br.com.marcoscunha.PokedexApi.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class PokemonController {
    @Autowired
    private PokemonService service;

    @GetMapping
    public List<Pokemon> getAll() {
        return service.getAllPokemons();
    }

    @GetMapping("/name")
    public List<Pokemon> getByName(@RequestParam String name) {
        return service.findByName(name);
    }

    @GetMapping("/type")
    public List<Pokemon> getByType(@RequestParam String type) {
        return service.findByType(type);
    }

    @GetMapping("/ability")
    public List<Pokemon> getByAbility(@RequestParam String ability) {
        return service.findByAbility(ability);
    }

    @GetMapping("/move")
    public List<Pokemon> getByMove(@RequestParam String move) {
        return service.findByMove(move);
    }

    @GetMapping("/generation")
    public List<Pokemon> getByGeneration(@RequestParam String generation) {
        return service.findByGeneration(generation);
    }

    @PostMapping("/save-all")
    public String importAll() {
        service.fetchAndSaveAllPokemons();
        return "Todos os Pokémons foram importados com sucesso!!";
    }
}
