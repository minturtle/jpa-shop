package jpabook.jpashop.service;

import jpabook.jpashop.dao.em.EntityManagerItemRepository;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.dto.ItemDto;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private EntityManagerItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item book;
    private Item album;
    private Item movie;

    private ItemDto bookDto;
    private ItemDto albumDto;
    private ItemDto movieDto;


    @BeforeEach
    void setup(){
        book = new Book("어린 왕자", 15000, "어린왕자 책", 30, "김민석", "11234");
        album = new Album("김민석 정규 앨범 7집", 50000, "앨범",10, "김민석", "김민석 데뷔 20주년 기념");
        movie = new Movie("어벤져스", 19000,"양화", 30, "김민석", "김민석");

        bookDto = new ItemDto.ItemDtoBuilder()
                .putItemField("어린 왕자", 15000, 30)
                .setItemType(Book.class)
                .putInheritedFields(book).build();

        albumDto = new ItemDto.ItemDtoBuilder()
                .putItemField("김민석 정규 앨범 7집", 50000, 10)
                .setItemType(Album.class)
                .putInheritedFields(album).build();

        movieDto = new ItemDto.ItemDtoBuilder()
                .putItemField("어벤져스", 19000, 30)
                .setItemType(Movie.class)
                .putInheritedFields(movie).build();
    }

    @Test
    @DisplayName("itemService 객체 생성")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(itemService).isNotNull();
        assertThat(itemRepository).isNotNull();
    }

    @Test
    @DisplayName("item 객체 저장")
    void t2() throws Exception {
        //given
        //when
        itemService.save(book);
        //then
    }


    @Test
    @DisplayName("저장한 item 객체 이름으로 조회")
    void t3() throws Exception {
        //given
        given(itemRepository.findByName("어벤져스")).willReturn(movie);
        given(itemRepository.findByName("어린 왕자")).willReturn(book);

        //when
        final ItemDto findMovie = itemService.findByName("어벤져스");
        final ItemDto findBook = itemService.findByName("어린 왕자");

        //then
        assertThat(findMovie.getName()).isEqualTo("어벤져스");
        assertThat(findMovie.getActor()).isEqualTo("김민석");
        assertThat(findMovie.getDirector()).isEqualTo("김민석");

        assertThat(findBook.getName()).isEqualTo("어린 왕자");
        assertThat(findBook.getAuthor()).isEqualTo("김민석");
        assertThat(findBook.getIsbn()).isEqualTo("11234");
    }

    @Test
    @Disabled
    @DisplayName("이름 조회, 잘못된 클래스 입력")
    void t4() throws Exception {
        //given
        given(itemRepository.findByName("어벤져스")).willReturn(movie);

        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->itemService.findByName("어벤져스");
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("찾은 상품과 같은 타입이 아닙니다.");
    }

    @Test
    @DisplayName("이름 조회, 상품을 찾을 수 없음")
    void t5() throws Exception {
        //given
        given(itemRepository.findByName("어벤져스")).willThrow(new EntityNotFoundException());

        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->itemService.findByName("어벤져스");
        //then

        assertThatThrownBy(throwableFunc).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("상품(책) 값 업데이트")
    void t6() throws Exception {
        //given
        given(itemRepository.findById(this.book.getId())).willReturn(book);
        //when

        bookDto.setName("어린 왕자2");
        bookDto.setStockQuantity(50);
        bookDto.setPrice(30000);
        bookDto.setAuthor("김민석2");
        bookDto.setIsbn("112233");

        itemService.updateItem(this.book.getId(), bookDto);
        //then
        assertThat(book.getName()).isEqualTo("어린 왕자2");
        assertThat(book.getStockQuantity()).isEqualTo(50);
        assertThat(book.getPrice()).isEqualTo(30000);
        assertThat(((Book)book).getAuthor()).isEqualTo("김민석2");
        assertThat(((Book)book).getIsbn()).isEqualTo("112233");
    }

    @Test
    @DisplayName("상품(앨범) 값 업데이트")
    void t7() throws Exception {
        //given
        given(itemRepository.findById(album.getId())).willReturn(album);

        //when
        albumDto.setName("김민석 정규 앨범 8집");
        albumDto.setStockQuantity(50);
        albumDto.setPrice(30000);
        albumDto.setArtist("김민석2");
        albumDto.setEtc("김민석 데뷔 25주년 기념");

        itemService.updateItem(album.getId(), albumDto);
        //then
        assertThat(album.getName()).isEqualTo("김민석 정규 앨범 8집");
        assertThat(album.getPrice()).isEqualTo(30000);
        assertThat(album.getStockQuantity()).isEqualTo(50);
        assertThat(((Album)album).getArtist()).isEqualTo("김민석2");
        assertThat(((Album)album).getEtc()).isEqualTo("김민석 데뷔 25주년 기념");
    }

    @Test
    @DisplayName("상품(영화)값 업데이트")
    void t8() throws Exception {
        //given
        movieDto.setName("어벤져스2");
        movieDto.setStockQuantity(40);
        movieDto.setPrice(13000);
        movieDto.setDirector("김민석2");
        movieDto.setActor("김민석3");

        given(itemRepository.findById(movie.getId())).willReturn(movie);
        //when
        itemService.updateItem(movie.getId(), movieDto);
        //then
        assertThat(movie.getName()).isEqualTo("어벤져스2");
        assertThat(movie.getPrice()).isEqualTo(13000);
        assertThat(movie.getStockQuantity()).isEqualTo(40);
        assertThat(((Movie)movie).getDirector()).isEqualTo("김민석2");
        assertThat(((Movie)movie).getActor()).isEqualTo("김민석3");
    }

    @Test
    @DisplayName("3개의 객체 저장 후 findAll로 값 반환받기-최신순")
    void t9() throws Exception {
        //given
        given(itemRepository.findAll(PageRequest.of(1, 10, getSortByType(ItemService.SortType.최신순))))
                .willReturn(List.of(book, album, movie));
        //when
        List<ItemDto> findItems = itemService.findAll(1,  ItemService.SortType.최신순);
        //then
        assertThat(findItems).contains(bookDto, Index.atIndex(0));
        assertThat(findItems).contains(albumDto, Index.atIndex(1));
        assertThat(findItems).contains(movieDto, Index.atIndex(2));
        assertThat(findItems.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("아이템 ID로 조회하기")
    void t10() throws Exception {
        //given
        given(itemRepository.findById(book.getId())).willReturn(book);
        //when
        final ItemDto findItemDto = itemService.findById(book.getId());
        //then
        assertThat(findItemDto.getId()).isEqualTo(book.getId());
    }

    private Sort getSortByType(ItemService.SortType sortType) {
        Sort sort;

        if(sortType.equals(ItemService.SortType.가격순)){
            sort = Sort.by("price").ascending();
        }
        else if(sortType.equals(ItemService.SortType.이름순)){
            sort = Sort.by("name").ascending();
        }
        else{
            sort = Sort.by("id").descending();
        }
        return sort;
    }
}