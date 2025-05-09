package br.com.marcoscunha.PokedexApi.controller;

import br.com.marcoscunha.PokedexApi.model.Pokemon;
import br.com.marcoscunha.PokedexApi.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    @Autowired
    private PokemonService service;

    @GetMapping("/paginated")
    public ResponseEntity<Page<Pokemon>> getPaginatedPokemons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(service.getAllPaginated(pageable));
    }

    @GetMapping
    public ResponseEntity<List<Pokemon>> getAll() {
        List<Pokemon> pokemons = service.getAllPokemons();
        return ResponseEntity.ok(pokemons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pokemon> getById(@PathVariable Long id) {
        Optional<Pokemon> result = Optional.ofNullable(service.findById(id));
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name")
    public ResponseEntity<List<Pokemon>> getByName(@RequestParam String name) {
        List<Pokemon> result = service.findByName(name);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/type")
    public ResponseEntity<List<Pokemon>> getByType(@RequestParam String type) {
        List<Pokemon> result = service.findByType(type);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ability")
    public ResponseEntity<List<Pokemon>> getByAbility(@RequestParam String ability) {
        List<Pokemon> result = service.findByAbility(ability);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/move")
    public ResponseEntity<List<Pokemon>> getByMove(@RequestParam String move) {
        List<Pokemon> result = service.findByMove(move);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importAll() {
        service.fetchAndSaveAllPokemons();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Todos os Pokémons foram importados com sucesso!!");
    }
}
