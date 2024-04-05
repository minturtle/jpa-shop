package jpabook.jpashop.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1389719531L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final jpabook.jpashop.domain.QBaseEntity _super = new jpabook.jpashop.domain.QBaseEntity(this);

    public final ListPath<Account, QAccount> accountList = this.<Account, QAccount>createList("accountList", Account.class, QAccount.class, PathInits.DIRECT2);

    public final QAddressInfo addressInfo;

    public final ListPath<jpabook.jpashop.domain.product.Cart, jpabook.jpashop.domain.product.QCart> cartList = this.<jpabook.jpashop.domain.product.Cart, jpabook.jpashop.domain.product.QCart>createList("cartList", jpabook.jpashop.domain.product.Cart.class, jpabook.jpashop.domain.product.QCart.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final QGoogleOAuth2AuthInfo googleOAuth2AuthInfo;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QKakaoOAuth2AuthInfo kakaoOAuth2AuthInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final ListPath<jpabook.jpashop.domain.order.Order, jpabook.jpashop.domain.order.QOrder> orderList = this.<jpabook.jpashop.domain.order.Order, jpabook.jpashop.domain.order.QOrder>createList("orderList", jpabook.jpashop.domain.order.Order.class, jpabook.jpashop.domain.order.QOrder.class, PathInits.DIRECT2);

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath uid = createString("uid");

    public final QUsernamePasswordAuthInfo usernamePasswordAuthInfo;

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.addressInfo = inits.isInitialized("addressInfo") ? new QAddressInfo(forProperty("addressInfo")) : null;
        this.googleOAuth2AuthInfo = inits.isInitialized("googleOAuth2AuthInfo") ? new QGoogleOAuth2AuthInfo(forProperty("googleOAuth2AuthInfo")) : null;
        this.kakaoOAuth2AuthInfo = inits.isInitialized("kakaoOAuth2AuthInfo") ? new QKakaoOAuth2AuthInfo(forProperty("kakaoOAuth2AuthInfo")) : null;
        this.usernamePasswordAuthInfo = inits.isInitialized("usernamePasswordAuthInfo") ? new QUsernamePasswordAuthInfo(forProperty("usernamePasswordAuthInfo")) : null;
    }

}

