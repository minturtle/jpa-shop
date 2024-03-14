package jpabook.jpashop.dto;

import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.item.*;

import lombok.Getter;

import lombok.Setter;

import java.util.Objects;


@Getter @Setter
public class ItemDto {

    private Class<? extends Item> itemType;

    private Long id;

    private String name;
    private int price;
    private String description;
    private int stockQuantity;

    //album
    private String artist;
    private String etc;

    //book
    private String author;
    private String isbn;

    //movie
    private String director;
    private String actor;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return price == itemDto.price && stockQuantity == itemDto.stockQuantity && Objects.equals(itemType, itemDto.itemType) && Objects.equals(name, itemDto.name) && Objects.equals(artist, itemDto.artist) && Objects.equals(etc, itemDto.etc) && Objects.equals(author, itemDto.author) && Objects.equals(isbn, itemDto.isbn) && Objects.equals(director, itemDto.director) && Objects.equals(actor, itemDto.actor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, name, price, stockQuantity, artist, etc, author, isbn, director, actor);
    }

    public static class ItemDtoBuilder{

        public ItemDtoBuilder putItemField(String name, int price, int stockQuantity){
            this.name = name;
            this.price = price;
            this.stockQuantity = stockQuantity;
            return this;
        }

        public ItemDtoBuilder setItemType(Class<? extends Item> clazz){
            this.itemType = clazz;
            return this;
        }

        public ItemDtoBuilder putItemId(Long id){
            this.id = id;
            return this;
        }

        public ItemDtoBuilder setDescription(String description){
            this.description = description;
            return this;
        }


        public ItemDtoBuilder putInheritedFields(Item findItem){
            if(itemType.equals(Album.class)){
                this.artist = ((Album)findItem).getArtist();
                this.etc = ((Album)findItem).getEtc();
            }
            else if(itemType.equals(Book.class)){
                this.author = ((Book)findItem).getAuthor();
                this.isbn = ((Book)findItem).getIsbn();
            }
            else if(itemType.equals(Movie.class)){
                this.director = ((Movie)findItem).getDirector();
                this.actor = ((Movie)findItem).getActor();
            }

            return this;
        }

        public ItemDto build() throws CheckItemType{
            validIsItemTypeFieldNull();

            ItemDto itemDto = new ItemDto();

            setDefaultItemField(itemDto);
            checkItemTypeAndSetFields(itemDto);

            return itemDto;
        }

        private void validIsItemTypeFieldNull() {
            if(itemType == null){
                throw new CheckItemType("ItemType 필드를 채워주세요");
            }
        }

        private void checkItemTypeAndSetFields(ItemDto itemDto) {
            itemDto.itemType = this.itemType;
            if(itemType.equals(Album.class)){
                itemDto.artist = artist;
                itemDto.etc = etc;
            }
            else if(itemType.equals(Book.class)){
                itemDto.author = author;
                itemDto.isbn = isbn;
            }
            else if(itemType.equals(Movie.class)){
                itemDto.director = director;
                itemDto.actor = actor;
            }
        }

        private void setDefaultItemField(ItemDto itemDto) {
            itemDto.id = id;
            itemDto.name = name;
            itemDto.price = price;
            itemDto.description = description;
            itemDto.stockQuantity = stockQuantity;
        }

        private Class<? extends Item> itemType;
        private Long id;
        private String name;
        private int price;
        private String description;
        private int stockQuantity;

        //album
        private String artist;
        private String etc;

        //book
        private String author;
        private String isbn;

        //movie
        private String director;
        private String actor;

    }



}
class CheckItemType extends RuntimeException{
    public CheckItemType(String message) {
        super(message);
    }
}