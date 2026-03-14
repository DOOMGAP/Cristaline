export interface Game {
  id: number;
  title: string;
  genre: string;
  releaseYear: number;
  description: string;
  coverUrl: string | null;
}

export interface GamePayload {
  title: string;
  genre: string;
  releaseYear: number;
  description: string;
  coverUrl: string | null;
}
