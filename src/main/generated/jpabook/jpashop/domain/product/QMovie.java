package jpabook.jpashop.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMovie is a Querydsl query type for Movie
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovie extends EntityPathBase<Movie> {

    private static final long serialVersionUID = 1889619598L;

    public static final QMovie movie = new QMovie("movie");

    public final QProduct _super = new QProduct(this);

    public final StringPath actor = createString("actor");

    //inherited
    public final ListPath<jpabook.jpashop.domain.Cart, jpabook.jpashop.domain.QCart> cartList = _super.cartList;

    //inherited
    public final ListPath<ProductCategory, QProductCategory> categories = _super.categories;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath description = _super.description;

    public final StringPath director = createString("director");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final NumberPath<Integer> price = _super.price;

    //inherited
    public final NumberPath<Integer> stockQuantity = _super.stockQuantity;

    //inherited
    public final StringPath thumbnailImageUrl = _super.thumbnailImageUrl;

    //inherited
    public final StringPath uid = _super.uid;

    public QMovie(String variable) {
        super(Movie.class, forVariable(variable));
    }

    public QMovie(Path<? extends Movie> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMovie(PathMetadata metadata) {
        super(Movie.class, metadata);
    }

}

