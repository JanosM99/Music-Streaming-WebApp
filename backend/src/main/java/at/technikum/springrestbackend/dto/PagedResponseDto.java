package at.technikum.springrestbackend.dto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PagedResponse DTO. Used to give a pageable to the frontend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDto<T> {
    private List<T> content;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}