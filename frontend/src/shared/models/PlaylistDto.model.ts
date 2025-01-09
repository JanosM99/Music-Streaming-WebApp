import { SongDto } from "./SongDto.model";

export interface PlaylistDto{
    id: number;
    name: string;
    userId: number;
    songs: SongDto[];
}