package jpabook.jpashop.service;

import jakarta.persistence.EntityNotFoundException;
import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.CategoryDto;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.common.InternalErrorException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.repository.CategoryRepository;
import jpabook.jpashop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Transactional
@RequiredArgsConstructor
@Loggable
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    /**
     * OFFSET 페이지네이션 기반 물건 리스트 검색
     * @author minseok kim
     * @param searchCondition 물품 검색 조건, 자세한 검색 조건은 요구사항에 맞춰 설정되어 있다.
     * @param pageable 페이지네이션 객체
    */
    public PaginationListDto<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Pageable pageable) {
        log.info("search product logic started");
        List<ProductDto.Preview> searchResult = productRepository.search(searchCondition, pageable);
        Long count = productRepository.getCount(searchCondition);

        log.info("search product logic finished");
        return PaginationListDto.<ProductDto.Preview>builder()
                .count(count)
                .data(searchResult)
                .build();
    }


    /**
     * CURSOR 페이지네이션 기반 물건 리스트 검색
     * @author minseok kim
     * @param searchCondition 물품 검색 조건, 자세한 검색 조건은 요구사항에 맞춰 설정되어 있다.
     * @param cursor 페이지 네이션 커서 정보
     * @param limit 가져오는 객체의 갯수
     */
    public List<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Optional<String> cursor, int limit) {
        log.info("search product logic started");
        List<ProductDto.Preview> searchResult = productRepository.search(searchCondition, cursor, limit);
        log.info("search product logic finished");
        return searchResult;
    }

    /**
     * 상품의 고유식별자로 상품의 상세 정보를 조회하는 메서드
     * @author minseok kim
     * @param givenUid 상품의 고유식별자
     * @return 상품의 상세정보
     * @exception CannotFindEntityException 고유식별자로 상품 조회에 실패한 경우
     * @exception InternalErrorException 상품의 타입이 올바르게 매핑되지 않은 경우
    */
    public ProductDto.Detail findByUid(String givenUid) throws CannotFindEntityException, InternalErrorException {
        log.info("find product by uid logic started");

        Product product = productRepository.findByUid(givenUid)
                .orElseThrow(()->new CannotFindEntityException(ProductExceptionMessages.CANNOT_FIND_PRODUCT.getMessage()));

        if(product instanceof Movie){
            log.info("find product by uid logic finished");
            return modelMapper.map(product, ProductDto.MovieDetail.class);
        }
        else if(product instanceof Album){
            log.info("find product by uid logic finished");
            return modelMapper.map(product, ProductDto.AlbumDetail.class);
        }
        else if(product instanceof Book){
            log.info("find product by uid logic finished");
            return modelMapper.map(product, ProductDto.BookDetail.class);
        }

        log.info("find product by uid logic finished");
        throw new InternalErrorException(ProductExceptionMessages.PRODUCT_TYPE_MAPPAING_FAILED.getMessage());
    }


    public List<CategoryDto.Info> getCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(category -> CategoryDto.Info.builder()
                        .uid(category.getUid())
                        .name(category.getName())
                        .productType(category.getProductType())
                        .build()).toList();
    }
}
