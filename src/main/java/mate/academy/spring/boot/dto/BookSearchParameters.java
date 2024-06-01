package mate.academy.spring.boot.dto;

public record BookSearchParameters(String[] titles, String[] authors, String[] isbn) {
}
