package at.technikum.springrestbackend.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 *  Creates a pageable with a certain sort order.
 */
public class PageableFactory {
    /**
     * Method that creates a pageable with sort functionality.
     *
     * @param page page.
     * @param size Page size.
     * @param sort Sort term.
     * @return pageable
     */
    public static Pageable create(int page, int size, String sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort.contains(",")) {
            String[] sortParams = sort.split(",");
            for (int i = 0; i < sortParams.length; i += 2) {
                String property = sortParams[i];
                String direction = sortParams[i + 1];
                orders.add(new Sort.Order(Sort.Direction.fromString(direction), property));
            }
        } else {
            orders.add(new Sort.Order(Sort.Direction.ASC, sort));
        }

        return PageRequest.of(page, size, Sort.by(orders));
    }

}

