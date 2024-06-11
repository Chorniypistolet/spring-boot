package mate.academy.spring.boot.dto;

public record BookSearchParameters(String[] title, String[] author, String[] isbn) {
}
