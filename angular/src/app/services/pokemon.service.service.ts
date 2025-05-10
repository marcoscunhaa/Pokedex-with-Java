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
  moves: string[];
  evolution: string[];
  sprite: string;
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

  constructor(private http: HttpClient) { }

  // Função para pegar pokémons com paginação
  getPokemons(page: number, limit: number): Observable<PagedResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    return this.http.get<any>(`${this.apiUrl}/paginated`, { params }).pipe(
      map(response => {
        console.log('Resposta da API:', response); // Verificação útil

        const pokemons: Pokemon[] = response.content ?? [];
        const totalPages: number = response.totalPages ?? 0;

        return { pokemons, totalPages } as PagedResponse;
      })
    );
  }


}
