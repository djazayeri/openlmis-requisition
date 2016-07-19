package org.openlmis.referencedata.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.UUID;

@StaticMetamodel(BaseEntity.class)
public abstract class BaseEntity_ {
  public static volatile SingularAttribute<BaseEntity, UUID> id;
}
