package mate.academy.spring.boot.mapper;

import mate.academy.spring.boot.config.MapperConfig;
import mate.academy.spring.boot.controller.CreateBookRequestDto;
import mate.academy.spring.boot.dto.BookDto;
import mate.academy.spring.boot.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
