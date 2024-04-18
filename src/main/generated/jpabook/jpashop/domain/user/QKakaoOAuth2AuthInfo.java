package jpabook.jpashop.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QKakaoOAuth2AuthInfo is a Querydsl query type for KakaoOAuth2AuthInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QKakaoOAuth2AuthInfo extends BeanPath<KakaoOAuth2AuthInfo> {

    private static final long serialVersionUID = 672746356L;

    public static final QKakaoOAuth2AuthInfo kakaoOAuth2AuthInfo = new QKakaoOAuth2AuthInfo("kakaoOAuth2AuthInfo");

    public final StringPath kakaoUid = createString("kakaoUid");

    public QKakaoOAuth2AuthInfo(String variable) {
        super(KakaoOAuth2AuthInfo.class, forVariable(variable));
    }

    public QKakaoOAuth2AuthInfo(Path<? extends KakaoOAuth2AuthInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKakaoOAuth2AuthInfo(PathMetadata metadata) {
        super(KakaoOAuth2AuthInfo.class, metadata);
    }

}

