package jpabook.jpashop.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 1870985483L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final jpabook.jpashop.domain.QBaseEntity _super = new jpabook.jpashop.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final jpabook.jpashop.domain.user.QAddressInfo deliveryInfo;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final ListPath<OrderProduct, QOrderProduct> orderProducts = this.<OrderProduct, QOrderProduct>createList("orderProducts", OrderProduct.class, QOrderProduct.class, PathInits.DIRECT2);

    public final QPayment payment;

    public final EnumPath<OrderStatus> status = createEnum("status", OrderStatus.class);

    public final StringPath uid = createString("uid");

    public final jpabook.jpashop.domain.user.QUser user;

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.deliveryInfo = inits.isInitialized("deliveryInfo") ? new jpabook.jpashop.domain.user.QAddressInfo(forProperty("deliveryInfo")) : null;
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment"), inits.get("payment")) : null;
        this.user = inits.isInitialized("user") ? new jpabook.jpashop.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

