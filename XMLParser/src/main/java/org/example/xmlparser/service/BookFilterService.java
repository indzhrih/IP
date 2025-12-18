package org.example.xmlparser.service;

import org.example.xmlparser.model.Book;
import org.example.xmlparser.model.BookFilterCriteria;

import java.util.Locale;
import java.util.function.Predicate;

public class BookFilterService {

    public Predicate<Book> buildFilterPredicate(BookFilterCriteria criteria) {
        return book -> {
            if (criteria == null) {
                return true;
            }

            String authorContains = normalize(criteria.getAuthorContains());
            if (!authorContains.isEmpty()) {
                String author = normalize(book.getAuthor());
                if (!author.contains(authorContains)) {
                    return false;
                }
            }

            Integer yearEquals = criteria.getYearEquals();
            if (yearEquals != null && yearEquals > 0) {
                if (book.getYear() != yearEquals) {
                    return false;
                }
            }

            String categoryContains = normalize(criteria.getCategoryContains());
            if (!categoryContains.isEmpty()) {
                String category = normalize(book.getCategory());
                if (!category.contains(categoryContains)) {
                    return false;
                }
            }

            return true;
        };
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
