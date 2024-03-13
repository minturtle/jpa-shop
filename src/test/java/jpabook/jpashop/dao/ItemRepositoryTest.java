package jpabook.jpashop.dao;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.service.ItemService;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item book;
    private Item album;
    private Item movie;

    @BeforeEach
    void setup(){
        book = new Book("어린 왕자", 15000, "어린왕자 책", 30, "김민석", "11234");
        album = new Album("김민석 정규 앨범 7집", 50000,"앨범", 10, "김민석", "김민석 데뷔 20주년 기념");
        movie = new Movie("어벤져스", 19000, "영화",30, "김민석", "김민석");

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
        saveAll();
        //then
        assertThat(book).isEqualTo(itemRepository.findById(book.getId()));
        assertThat(movie).isEqualTo(itemRepository.findById(movie.getId()));
        assertThat(album).isEqualTo(itemRepository.findById(album.getId()));
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

    @Test
    @DisplayName("이름으로 조회, 존재하지 않는 경우")
    void t5() throws Exception {
        //given
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->itemRepository.findByName("아무개");
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("3개의 객체 저장하고 리스트로 반환받기")
    void t6() throws Exception {
        //given
        saveAll();
        //when
        List<Item> findItems = itemRepository.findAll();
        //then
        assertThat(findItems).contains(book, Index.atIndex(0));
        assertThat(findItems).contains(movie, Index.atIndex(1));
        assertThat(findItems).contains(album, Index.atIndex(2));
        assertThat(findItems.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("3개의 객체 저장하고 최신순으로 조회하기")
    void t7() throws Exception {
        //given
        saveAll();
        //when
        List<Item> findItems = itemRepository
                .findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        //then
        assertThat(findItems).contains(book, Index.atIndex(2));
        assertThat(findItems).contains(movie, Index.atIndex(1));
        assertThat(findItems).contains(album, Index.atIndex(0));
        assertThat(findItems.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("3개의 객체 저장하고 이름순으로 조회하기")
    void t8() throws Exception {
        //given
        saveAll();
        //when
        List<Item> findItems = itemRepository
                .findAll(PageRequest.of(0, 10, Sort.by("name").ascending()));
        //then
        assertThat(findItems).contains(book, Index.atIndex(1));
        assertThat(findItems).contains(movie, Index.atIndex(2));
        assertThat(findItems).contains(album, Index.atIndex(0));
        assertThat(findItems.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("3개의 객체 저장하고 가격순으로 정렬하기")
    void t9() throws Exception {
        //given
        saveAll();
        //when
        List<Item> findItems = itemRepository
                .findAll(PageRequest.of(0, 10, Sort.by("price").ascending()));
        //then
        assertThat(findItems).contains(book, Index.atIndex(0));
        assertThat(findItems).contains(movie, Index.atIndex(1));
        assertThat(findItems).contains(album, Index.atIndex(2));
        assertThat(findItems.size()).isEqualTo(3);

    }

    private void saveAll() {
        itemRepository.save(book);
        itemRepository.save(movie);
        itemRepository.save(album);
    }
}