package jpabook.jpashop.dao;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

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
    @DisplayName("itemRepository 객체 생성 확인")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(itemRepository).isNotNull();
    }

    @Test
    @DisplayName("itemRepository로 book, movie, album 객체 저장하고 조회하기")
    void t2() throws Exception {
        //given
        //when
        itemRepository.save(book);
        itemRepository.save(movie);
        itemRepository.save(album);
        //then

        assertThat(book).isEqualTo(itemRepository.findById(book.getId()).get());
        assertThat(movie).isEqualTo(itemRepository.findById(movie.getId()).get());
        assertThat(album).isEqualTo(itemRepository.findById(album.getId()).get());
    }

    @Test
    @DisplayName("book 객체 update하기")
    void t3() throws Exception {
        //given
        itemRepository.save(book);
        Item findBook = itemRepository.findById(this.book.getId()).get();

        //when
        findBook.setName("심청전");
        //then
        Item findItem2 = itemRepository.findById(this.book.getId()).get();

        assertThat(findItem2.getName()).isEqualTo("심청전");
        assertThat(findItem2.getPrice()).isEqualTo(15000);
        assertThat(findItem2.getStockQuantity()).isEqualTo(30);
    }

    @Test
    @DisplayName("이름으로 저장한 item 조회하기")
    void t4() throws Exception {
        //given
        itemRepository.save(album);
        //when
        Item findItem = itemRepository.findByName("김민석 정규 앨범 7집");
        //then
        assertThat(findItem).isEqualTo(album);

    }
}