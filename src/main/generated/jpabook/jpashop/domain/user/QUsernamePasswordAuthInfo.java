package jpabook.jpashop.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUsernamePasswordAuthInfo is a Querydsl query type for UsernamePasswordAuthInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUsernamePasswordAuthInfo extends BeanPath<UsernamePasswordAuthInfo> {

    private static final long serialVersionUID = -939291833L;

    public static final QUsernamePasswordAuthInfo usernamePasswordAuthInfo = new QUsernamePasswordAuthInfo("usernamePasswordAuthInfo");

    public final StringPath password = createString("password");

    public final StringPath username = createString("username");

    public QUsernamePasswordAuthInfo(String variable) {
        super(UsernamePasswordAuthInfo.class, forVariable(variable));
    }

    public QUsernamePasswordAuthInfo(Path<? extends UsernamePasswordAuthInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUsernamePasswordAuthInfo(PathMetadata metadata) {
        super(UsernamePasswordAuthInfo.class, metadata);
    }

}

