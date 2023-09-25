package ewm.main.service.common.pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationCalculator {
    public static Pageable getPage(int from, int size) {
        int pageNumber = from / size;
        return PageRequest.of(pageNumber, size);
    }
}