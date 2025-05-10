import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { Pokemon, PokemonService, PagedResponse } from '../../services/pokemon.service.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  pokemons: Pokemon[] = [];
  currentPage: number = 0;
  limit: number = 9;
  loading: boolean = false;
  endReached: boolean = false;
  totalPages: number = 0;

  constructor(private pokemonService: PokemonService) {}

  ngOnInit(): void {
    this.loadPokemons();
  }

  loadPokemons(): void {
    if (this.loading || this.endReached) return;

    this.loading = true;

    this.pokemonService.getPokemons(this.currentPage, this.limit).subscribe(
      (response: PagedResponse) => {
        console.log('Resposta da API:', response);

        if (!response.pokemons || response.pokemons.length === 0) {
          this.endReached = true;
        } else {
          this.pokemons = [...this.pokemons, ...response.pokemons];
          this.currentPage++;
          this.totalPages = response.totalPages;
        }

        this.loading = false;
      },
      (error) => {
        console.error('Erro ao carregar pokémons', error);
        this.loading = false;
      }
    );
  }

  getTypeColor(type: string): string {
    switch (type.toLowerCase()) {
      case 'fire':
        return 'text-orange-500';
      case 'flying':
        return 'text-blue-300';
      case 'water':
        return 'text-blue-500';
      case 'ice':
        return 'text-blue-700';
      case 'grass':
        return 'text-green-500';
      case 'bug':
        return 'text-green-700';
      case 'ground':
        return 'text-yellow-500';
      case 'electric':
        return 'text-yellow-300';
      case 'dragon':
        return 'text-red-500';
      case 'rock':
        return 'text-yellow-700';
      case 'psychic':
        return 'text-pink-700';
      case 'fairy':
        return 'text-pink-500';
      case 'ghost':
        return 'text-purple-500';
      case 'poison':
        return 'text-purple-700';
      case 'dark':
        return 'text-black';
      default:
        return 'text-gray-500';
    }
  }
}
