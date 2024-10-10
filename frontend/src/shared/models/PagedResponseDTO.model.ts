export interface PagedResponseDto<T> {
    content: T[];
    currentPage: number;
    totalItems: number;
    totalPages: number;
}