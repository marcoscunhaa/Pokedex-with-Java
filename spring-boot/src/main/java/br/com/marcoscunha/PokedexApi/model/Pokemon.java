package br.com.marcoscunha.PokedexApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pokemons")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private int height;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private String sprite;

    @Lob
    @Column(name = "sprite_base64", columnDefinition = "CLOB")
    private String spriteBase64;


    @ElementCollection
    @CollectionTable(name = "pokemon_types", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "type")
    private List<String> type;

    @ElementCollection
    @CollectionTable(name = "pokemon_abilities", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "ability")
    private List<String> ability;

    @ElementCollection
    @CollectionTable(name = "pokemon_moves", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "move")
    private List<String> move;

    @ElementCollection
    @CollectionTable(name = "pokemon_stats", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "stat")
    private Map<String, Integer> stats;

    @ElementCollection
    @CollectionTable(name = "pokemon_evolutions", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "evolution")
    private List<String> evolution;

    @Column(nullable = true, length = 50)
    private String generation;
}
