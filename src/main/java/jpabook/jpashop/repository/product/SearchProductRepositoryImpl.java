package jpabook.jpashop.repository.product;


import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.product.Product;
import jpabook.jpashop.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

import static jpabook.jpashop.domain.product.QProduct.product;

@Repository
@RequiredArgsConstructor
public class SearchProductRepositoryImpl implements SearchProductRepository {


    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> search(ProductDto.SearchCondition searchCondition) {

        JPAQuery<Product> query = jpaQueryFactory.select(product)
                .from(product);

        if(searchCondition.getName().isPresent()){
            String searchName = searchCondition.getName().get();

            query.where(product.name.contains(searchName));
        }



        return query.fetch();


    }
}
