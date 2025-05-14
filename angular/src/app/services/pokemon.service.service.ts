import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Pokemon {
  id: number;
  name: string;
  description: string;
  height: number;
  weight: number;
  type: string[];
  stats: { [key: string]: number };
  ability: string[];
  move: string[];
  evolution: string[];
  spriteBase64: string;
  generation: string;
}

export interface PagedResponse {
  pokemons: Pokemon[];
  totalPages: number;
}

@Injectable({
  providedIn: 'root'
})
export class PokemonService {

  private apiUrl = 'http://localhost:8080/api/pokemons';

  constructor(private http: HttpClient) {}

  // 🔁 Pega todos os pokémons sem paginação
  getAllPokemons(): Observable<Pokemon[]> {
    return this.http.get<Pokemon[]>(`${this.apiUrl}/all`);
  }

  // 📄 Pega pokémons com paginação
  getPokemons(page: number, limit: number): Observable<PagedResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<any>(`${this.apiUrl}`, { params }).pipe(
      map(response => {
        const pokemons: Pokemon[] = response.content ?? [];
        const totalPages: number = response.totalPages ?? 0;
        return { pokemons, totalPages } as PagedResponse;
      })
    );
  }

  // 🔍 Buscar por ID
  getPokemonById(id: number): Observable<Pokemon> {
    return this.http.get<Pokemon>(`${this.apiUrl}/${id}`);
  }

  // 🔍 Buscar por nome
  getPokemonsByName(name: string): Observable<Pokemon[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Pokemon[]>(`${this.apiUrl}/name`, { params });
  }

  // 🔍 Buscar por tipo
  getPokemonsByType(type: string): Observable<Pokemon[]> {
    const params = new HttpParams().set('type', type);
    return this.http.get<Pokemon[]>(`${this.apiUrl}/type`, { params });
  }

  // 🔍 Buscar por habilidade
  getPokemonsByAbility(ability: string): Observable<Pokemon[]> {
    const params = new HttpParams().set('ability', ability);
    return this.http.get<Pokemon[]>(`${this.apiUrl}/ability`, { params });
  }

  // 🔍 Buscar por movimento
  getPokemonsByMove(move: string): Observable<Pokemon[]> {
    const params = new HttpParams().set('move', move);
    return this.http.get<Pokemon[]>(`${this.apiUrl}/move`, { params });
  }

  // 🔍 Pesquisa avançada com múltiplos filtros (nome, múltiplos tipos, habilidade, movimento)
  searchAdvancedPokemons(filters: {
    name?: string;
    types?: string[];
    ability?: string;
    move?: string;
    generation?: string;
  }): Observable<Pokemon[]> {
    let params = new HttpParams();

    if (filters.name) {
      params = params.set('name', filters.name);
    }

    if (filters.types && filters.types.length > 0) {
      filters.types.forEach(type => {
        params = params.append('types', type);
      });
    }

    if (filters.ability) {
      params = params.set('ability', filters.ability);
    }

    if (filters.move) {
      params = params.set('move', filters.move);
    }

    if (filters.generation) {
      params = params.set('generation', filters.generation);
    }

    return this.http.get<Pokemon[]>(`${this.apiUrl}/search/advanced`, { params });
  }
}
