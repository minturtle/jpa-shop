package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item book;
    private Item album;
    private Item movie;

    @BeforeEach
    void setup(){
        book = new Book("어린 왕자", 15000, 30, "김민석", "11234");
        album = new Album("김민석 정규 앨범 7집", 50000, 10, "김민석", "김민석 데뷔 20주년 기념");
        movie = new Movie(30, 19000, "어벤져스", "김민석", "김민석");

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
        Item item = itemService.save(book);
        //then
        assertThat(item).isEqualTo(book);
    }


    @Test
    @DisplayName("저장한 item 객체 이름으로 조회")
    void t3() throws Exception {
        //given

        given(itemRepository.findByName("어벤져스")).willReturn(movie);
        given(itemRepository.findByName("어린 왕자")).willReturn(book);

        //when
        Movie findMovie = itemService.findByName("어벤져스", Movie.class);
        Book findBook = itemService.findByName("어린 왕자", Book.class);
        //then
        assertThat(movie).isEqualTo(findMovie);
        assertThat(book).isEqualTo(findBook);
    }

    @Test
    @DisplayName("이름 조회, 잘못된 클래스 입력")
    void t4() throws Exception {
        //given
        given(itemRepository.findByName("어벤져스")).willReturn(movie);

        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->itemService.findByName("어벤져스", Member.class);
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("찾은 상품과 같은 타입이 아닙니다.");
    }

    @Test
    @DisplayName("이름 조회, 상품을 찾을 수 없음")
    void t5() throws Exception {
        //given
        given(itemRepository.findByName("어벤져스")).willReturn(null);

        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->itemService.findByName("어벤져스", Movie.class);
        //then

        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당되는 상품을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("상품(책) 값 업데이트")
    void t6() throws Exception {
        //given
        Book modifiedBook = new Book("어린 왕자2", 30000, 50, "김민석2", "112233");
        given(itemRepository.findById(this.book.getId())).willReturn(Optional.of(this.book));
        //when
        itemService.updateItem(this.book.getId(), modifiedBook);
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
        Album modifiedAlbum = new Album("김민석 정규 앨범 8집", 30000, 50, "김민석2", "김민석 데뷔 25주년 기념");
        given(itemRepository.findById(album.getId())).willReturn(Optional.of(album));
        //when
        itemService.updateItem(album.getId(), modifiedAlbum);
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
        Movie modifiedMovie = new Movie(40, 13000, "어벤져스2", "김민석2", "김민석3");
        given(itemRepository.findById(movie.getId())).willReturn(Optional.of(movie));
        //when
        itemService.updateItem(movie.getId(), modifiedMovie);
        //then
        assertThat(movie.getName()).isEqualTo("어벤져스2");
        assertThat(movie.getPrice()).isEqualTo(13000);
        assertThat(movie.getStockQuantity()).isEqualTo(40);
        assertThat(((Movie)movie).getDirector()).isEqualTo("김민석2");
        assertThat(((Movie)movie).getActor()).isEqualTo("김민석3");
    }
}