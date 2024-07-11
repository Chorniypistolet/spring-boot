package mate.academy.spring.boot.dto.book;

public record BookSearchParameters(String[] title, String[] author, String[] isbn) {
}
