package jpabook.jpashop.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddressInfo is a Querydsl query type for AddressInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAddressInfo extends BeanPath<AddressInfo> {

    private static final long serialVersionUID = -456265054L;

    public static final QAddressInfo addressInfo = new QAddressInfo("addressInfo");

    public final StringPath address = createString("address");

    public final StringPath detailedAddress = createString("detailedAddress");

    public QAddressInfo(String variable) {
        super(AddressInfo.class, forVariable(variable));
    }

    public QAddressInfo(Path<? extends AddressInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddressInfo(PathMetadata metadata) {
        super(AddressInfo.class, metadata);
    }

}

