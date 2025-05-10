import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { Pokemon, PokemonService } from '../../services/pokemon.service.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  pokemons: Pokemon[] = [];
  currentPage: number = 1;
  limit: number = 9;
  loading: boolean = false;
  endReached: boolean = false;

  constructor(private pokemonService: PokemonService) {}

  ngOnInit(): void {
    this.loadPokemons();
  }

  loadPokemons(): void {
    if (this.loading || this.endReached) return;

    this.loading = true;

    this.pokemonService.getPokemons(this.currentPage, this.limit).subscribe(
      (data) => {
        if (data.length === 0) {
          this.endReached = true; // Não há mais pokémons
        } else {
          this.pokemons = [...this.pokemons, ...data];
          this.currentPage++;
        }
        this.loading = false;
      },
      (error) => {
        console.error('Erro ao carregar pokémons', error);
        this.loading = false;
      }
    );
  }
}
