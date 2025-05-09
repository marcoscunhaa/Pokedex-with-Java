import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pokemon {
  id: number;
  name: string;
  description: string;
  height: number;
  weight: number;
  types: string[];
  stats: { [key: string]: number };
  abilitys: string[];
  moves: string[];
  evolution: string[];  
  sprite: string;
}

@Injectable({
  providedIn: 'root'
})
export class PokemonService {

  private apiUrl = 'http://localhost:8080/api/pokemons';

  constructor(private http: HttpClient) {}

  getAllPokemons(): Observable<Pokemon[]> {
    return this.http.get<Pokemon[]>(this.apiUrl);
  }

  getPokemonById(id: number): Observable<Pokemon> {
    return this.http.get<Pokemon>(`${this.apiUrl}/${id}`);
  }
}
