package book.store.repository.book.spec;

import book.store.model.Book;
import book.store.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return "isbn";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) ->
                root.get("isbn").in(Arrays.stream(params).toArray()));
    }
}
