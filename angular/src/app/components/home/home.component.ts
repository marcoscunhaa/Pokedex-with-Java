import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Pokemon, PokemonService } from '../../services/pokemon.service.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  allPokemons: Pokemon[] = [];
  displayedPokemons: Pokemon[] = [];
  searchTerm: string = '';
  currentPage: number = 0;
  limit: number = 9;
  totalPages: number = 0;
  loading: boolean = false;
  endReached: boolean = false;

  constructor(private pokemonService: PokemonService) { }

  ngOnInit(): void {
    this.loadAllPokemons();
  }

  loadAllPokemons(): void {
    this.loading = true;
    this.pokemonService.getAllPokemons().subscribe(
      (pokemons: Pokemon[]) => {
        this.allPokemons = pokemons;
        this.totalPages = Math.ceil(this.allPokemons.length / this.limit);
        this.updateDisplayedPokemons();
        this.loading = false;
      },
      (error) => {
        console.error('Erro ao carregar todos os pokémons:', error);
        this.loading = false;
      }
    );
  }

  updateDisplayedPokemons(): void {
    const startIndex = this.currentPage * this.limit;
    const endIndex = startIndex + this.limit;
    this.displayedPokemons = this.filteredPokemons.slice(0, endIndex);

    this.endReached = endIndex >= this.filteredPokemons.length;
  }

  get filteredPokemons(): Pokemon[] {
    const term = this.searchTerm.toLowerCase().trim();
    return this.allPokemons.filter(pokemon => {
      const matchesNameOrId = pokemon.name.toLowerCase().includes(term) || pokemon.id.toString().includes(term);
      return matchesNameOrId;
    });
  }

  loadPokemons(): void {
    if (this.loading || this.endReached) return;

    this.loading = true;
    setTimeout(() => {
      this.currentPage++;
      this.updateDisplayedPokemons();
      this.loading = false;
    }, 500);
  }

  onSearchTermChange(): void {
    this.currentPage = 0;
    this.endReached = false;
    this.totalPages = Math.ceil(this.filteredPokemons.length / this.limit);
    this.updateDisplayedPokemons();
  }

  getTypeColor(type: string): string {
    switch (type.toLowerCase()) {
      case 'fire': return 'text-orange-500';
      case 'fighting': return 'text-orange-700';
      case 'flying': return 'text-blue-300';
      case 'water': return 'text-blue-500';
      case 'ice': return 'text-blue-700';
      case 'grass': return 'text-green-500';
      case 'bug': return 'text-green-700';
      case 'ground': return 'text-yellow-500';
      case 'electric': return 'text-yellow-300';
      case 'dragon': return 'text-red-500';
      case 'rock': return 'text-yellow-700';
      case 'psychic': return 'text-pink-700';
      case 'fairy': return 'text-pink-500';
      case 'ghost': return 'text-purple-500';
      case 'poison': return 'text-purple-700';
      case 'dark': return 'text-black';
      default: return 'text-gray-500';
    }
  }

  trackPokemon(index: number, pokemon: Pokemon): number {
    return pokemon.id;
  }

  // ----------------------
  // Busca Avançada
  // ----------------------

  showAdvancedSearch: boolean = false;

  availableTypes: string[] = [
    'normal', 'fire', 'water', 'grass', 'electric', 'ice',
    'fighting', 'poison', 'ground', 'flying', 'psychic',
    'bug', 'rock', 'ghost', 'dark', 'dragon', 'steel', 'fairy'
  ];

  selectedTypes: string[] = [];

  advancedSearch = {
    ability: '',
    move: ''
  };

  toggleTypeSelection(type: string): void {
    if (this.selectedTypes.includes(type)) {
      this.selectedTypes = this.selectedTypes.filter(t => t !== type);
    } else {
      this.selectedTypes.push(type);
    }
  }

  resetAdvancedSearch(): void {
    this.selectedTypes = [];
    this.advancedSearch.ability = '';
    this.advancedSearch.move = '';
    this.searchTerm = '';
    this.showAdvancedSearch = false;
    this.loadAllPokemons();
  }

  applyAdvancedSearch(): void {
    this.loading = true;
    this.currentPage = 0;
    this.endReached = false;

    const filters = {
      name: this.searchTerm.trim() || undefined,
      types: this.selectedTypes.length > 0 ? this.selectedTypes : undefined,
      ability: this.advancedSearch.ability.trim() || undefined,
      move: this.advancedSearch.move.trim() || undefined
    };


    this.pokemonService.searchAdvancedPokemons(filters).subscribe(
      (pokemons: Pokemon[]) => {
        this.allPokemons = pokemons;
        this.totalPages = Math.ceil(pokemons.length / this.limit);
        this.updateDisplayedPokemons();
        this.loading = false;
      },
      (error) => {
        console.error('Erro ao realizar busca avançada:', error);
        this.loading = false;
      }
    );

    this.showAdvancedSearch = false;
  }

}
