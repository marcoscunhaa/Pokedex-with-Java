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
  loading: boolean = false; // <- adicionado
  endReached: boolean = false; // <- adicionado

  constructor(private pokemonService: PokemonService) {}

  ngOnInit(): void {
    this.pokemonService.getAllPokemons().subscribe(
      (pokemons: Pokemon[]) => {
        this.allPokemons = pokemons;
        this.totalPages = Math.ceil(this.allPokemons.length / this.limit);
        this.updateDisplayedPokemons();
      },
      (error) => {
        console.error('Erro ao carregar todos os pokémons:', error);
      }
    );
  }

  // Atualiza os pokémons mostrados na tela com base na página atual
  updateDisplayedPokemons(): void {
    const startIndex = this.currentPage * this.limit;
    const endIndex = startIndex + this.limit;
    this.displayedPokemons = this.filteredPokemons.slice(0, endIndex);

    // Verifica se todos os pokémons já foram carregados
    if (endIndex >= this.filteredPokemons.length) {
      this.endReached = true;
    }
  }

  // Getter para aplicar filtro pelo termo de pesquisa
  get filteredPokemons(): Pokemon[] {
    const term = this.searchTerm.toLowerCase().trim();
    return this.allPokemons.filter(pokemon => {
      const matchesNameOrId = pokemon.name.toLowerCase().includes(term) || pokemon.id.toString().includes(term);
      return matchesNameOrId;
    });
  }

  // Paginação manual — chamada ao clicar em "Carregar mais"
  loadPokemons(): void {
    if (this.loading || this.endReached) return;

    this.loading = true;
    setTimeout(() => {
      this.currentPage++;
      this.updateDisplayedPokemons();
      this.loading = false;
    }, 500); // Simula carregamento
  }

  // Atualiza exibição quando o termo de busca muda
  onSearchTermChange(): void {
    this.currentPage = 0;
    this.endReached = false;
    this.totalPages = Math.ceil(this.filteredPokemons.length / this.limit);
    this.updateDisplayedPokemons();
  }

  // Define cor do tipo
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
}
