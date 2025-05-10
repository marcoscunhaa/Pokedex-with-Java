import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pokemon {
  id: number;
  name: string;
  description: string;
  height: number;
  weight: number;
  type: string[];
  stats: { [key: string]: number };
  ability: string[];
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

  // Função para pegar pokémons com paginação
  getPokemons(page: number = 1, limit: number = 9): Observable<Pokemon[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<Pokemon[]>(this.apiUrl, { params });
  }
}
