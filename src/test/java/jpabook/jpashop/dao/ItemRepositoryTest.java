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
        book = new Book(30, 15000, "어린 왕자", "김민석", "11234");
        album = new Album(10, 50000, "김민석 정규 앨범 7집", "김민석", "김민석 데뷔 20주년 기념");
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
}