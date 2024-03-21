package jpabook.jpashop.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGoogleOAuth2AuthInfo is a Querydsl query type for GoogleOAuth2AuthInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QGoogleOAuth2AuthInfo extends BeanPath<GoogleOAuth2AuthInfo> {

    private static final long serialVersionUID = 2029184682L;

    public static final QGoogleOAuth2AuthInfo googleOAuth2AuthInfo = new QGoogleOAuth2AuthInfo("googleOAuth2AuthInfo");

    public final StringPath googleUid = createString("googleUid");

    public QGoogleOAuth2AuthInfo(String variable) {
        super(GoogleOAuth2AuthInfo.class, forVariable(variable));
    }

    public QGoogleOAuth2AuthInfo(Path<? extends GoogleOAuth2AuthInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGoogleOAuth2AuthInfo(PathMetadata metadata) {
        super(GoogleOAuth2AuthInfo.class, metadata);
    }

}

