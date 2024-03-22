package jpabook.jpashop.repository.product;


import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.SortOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

import static jpabook.jpashop.domain.product.QProduct.product;
import static jpabook.jpashop.domain.QBaseEntity.baseEntity;


@Repository
@RequiredArgsConstructor
public class SearchProductRepositoryImpl implements SearchProductRepository {


    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> search(ProductDto.SearchCondition searchCondition, Pageable pageable) {

        JPAQuery<Product> query = jpaQueryFactory.select(product)
                .from(product);

        if(searchCondition.getName().isPresent()){
            String searchName = searchCondition.getName().get();

            query.where(product.name.contains(searchName));
        }


        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());


        switch (searchCondition.getSortOption()){
            case BY_DATE -> query.orderBy(baseEntity.createdAt.desc());
            case BY_NAME -> query.orderBy(product.name.asc());
            case BY_PRICE -> query.orderBy(product.price.asc());
        }

        return query.fetch();


    }
}
