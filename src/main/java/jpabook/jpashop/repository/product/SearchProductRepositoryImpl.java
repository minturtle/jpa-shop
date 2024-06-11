package jpabook.jpashop.repository.product;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.product.Album;
import jpabook.jpashop.domain.product.Book;
import jpabook.jpashop.domain.product.Movie;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.SortOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static jpabook.jpashop.domain.product.QProduct.product;
import static jpabook.jpashop.domain.product.QProductCategory.productCategory;
import static jpabook.jpashop.domain.product.QCategory.category;


@Repository
@RequiredArgsConstructor
public class SearchProductRepositoryImpl implements SearchProductRepository {


    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Pageable pageable) {

        JPAQuery<ProductDto.Preview> query = jpaQueryFactory.select(Projections.constructor(
                ProductDto.Preview.class,
                        product.uid,
                        product.name,
                        product.price,
                        product.thumbnailImageUrl,
                        product.createdAt
                        ))
                .from(product);

        setUpWherePredicationQueries(query, searchCondition);

        setUpPagenationQuries(query, pageable, searchCondition.getSortOption());

        return query.fetch();


    }


    @Override
    public List<ProductDto.Preview> search(ProductDto.SearchCondition searchCondition, Optional<String> cursor, int limit) {
        JPAQuery<ProductDto.Preview> query = jpaQueryFactory.select(Projections.constructor(
                        ProductDto.Preview.class,
                        product.uid,
                        product.name,
                        product.price,
                        product.thumbnailImageUrl,
                        product.createdAt
                    )
                )
                .from(product);

        setUpWherePredicationQueries(query, searchCondition);

        setUpPaginationQueries(query, cursor, limit, searchCondition.getSortOption());

        return query.fetch();


    }


    @Override
    public Long getCount(ProductDto.SearchCondition searchCondition) {
        JPAQuery<Long> query = jpaQueryFactory.select(product.count())
                .from(product);

        setUpWherePredicationQueries(query, searchCondition);

        return query.fetchOne();
    }

    private void setUpWherePredicationQueries(JPAQuery query, ProductDto.SearchCondition searchCondition) {
        if(searchCondition.getName().isPresent()){
            String searchName = searchCondition.getName().get();

            query.where(product.name.contains(searchName));
        }
        if(searchCondition.getCategoryUid().isPresent()){
            String searchCategoryUid = searchCondition.getCategoryUid().get();


            // category의 uid를 한번에 조회하기 위해 productCategory와 category를 left join
            query
                    .leftJoin(product.categories, productCategory)
                    .leftJoin(productCategory.category, category);

            query.where(category.uid.eq(searchCategoryUid));

        }if(searchCondition.getPriceRange().isPresent()){
            ProductDto.PriceRange priceRange = searchCondition.getPriceRange().get();
            query.where(product.price.goe(priceRange.getMinPrice()));
            query.where(product.price.loe(priceRange.getMaxPrice()));

        }
        switch (searchCondition.getProductType()){
            case ALL -> {
            }
            case BOOK -> {
                query.where(product.dtype.eq("BOOK"));
            }
            case ALBUM -> {
                query.where(product.dtype.eq("ALBUM"));
            }
            case MOVIE -> {
                query.where(product.dtype.eq("MOVIE"));
            }
        }

    }


    private void setUpPaginationQueries(JPAQuery query, Optional cursorOptional, int limit, SortOption sortOption) {
        switch (sortOption){
            case BY_DATE:
                setUpDatePagination(query, cursorOptional);
                break;
            case BY_NAME:
                setUpNamePagination(query, cursorOptional);
                break;
            case BY_PRICE:
                setUpPricePagination(query, cursorOptional);
                break;
        }

        query.limit(limit);
        query.offset(0L);
    }


    private void setUpPagenationQuries(JPAQuery query, Pageable pageable, SortOption sortOption) {
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());


        switch (sortOption){
            case BY_DATE -> query.orderBy(product.createdAt.desc());
            case BY_NAME -> query.orderBy(product.name.asc());
            case BY_PRICE -> query.orderBy(product.price.asc());
        }
    }
    private static void setUpPricePagination(JPAQuery query, Optional cursorOptional) {
        if(cursorOptional.isPresent() && !(cursorOptional.get() instanceof Integer)){
            throw new IllegalArgumentException("cursor type is not matched");
        }

        if(cursorOptional.isPresent()){
            query.where(product.price.lt((Integer) cursorOptional.get()));
        }


        query.orderBy(product.price.asc());
    }

    private static void setUpNamePagination(JPAQuery query, Optional cursorOptional) {
        if(cursorOptional.isPresent() && !(cursorOptional.get() instanceof String)){
            throw new IllegalArgumentException("cursor type is not matched");
        }


        if(cursorOptional.isPresent()){
            query.where(product.name.gt((String) cursorOptional.get()));
        }

        query.orderBy(product.name.asc());
        return;
    }

    private static void setUpDatePagination(JPAQuery query, Optional cursorOptional) {
        if(cursorOptional.isPresent() && !(cursorOptional.get() instanceof LocalDateTime)){
            throw new IllegalArgumentException("cursor type is not matched");
        }

        if(cursorOptional.isPresent()){
            query.where(product.createdAt.before((LocalDateTime) cursorOptional.get()));
        }
        query.orderBy(product.createdAt.desc());
    }




}
