/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.marketdatasnapshot;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.PropertyDefinition;

import com.opengamma.id.UniqueId;
import com.opengamma.id.UniqueIdentifiable;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.joda.beans.Bean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.impl.BasicImmutableBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * A key used to identify a volatility surface.
 * <p>
 * This class is immutable and thread-safe.
 */
@BeanDefinition
public final class VolatilitySurfaceKey implements ImmutableBean, StructuredMarketDataKey, Comparable<VolatilitySurfaceKey>, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 3L;

  /**
   * The target.
   */
  @PropertyDefinition
  private final UniqueId _target;
  /**
   * The curve name.
   */
  @PropertyDefinition
  private final String _name;
  /**
   * The instrument type.
   */
  @PropertyDefinition
  private final String _instrumentType;
  /**
   * The quote type.
   */
  @PropertyDefinition
  private final String _quoteType;
  /**
   * The quote units.
   */
  @PropertyDefinition
  private final String _quoteUnits;

  /**
   * Creates an instance.
   * 
   * @param target  the target
   * @param name  the name
   * @param instrumentType  the instrument type
   * @param quoteType the quote type
   * @param quoteUnits the quote units
   */
  @ImmutableConstructor
  private VolatilitySurfaceKey(final UniqueIdentifiable target, final String name, final String instrumentType, final String quoteType, final String quoteUnits) {
    ArgumentChecker.notNull(target, "target");
    _target = target.getUniqueId();
    _name = name;
    _instrumentType = instrumentType;
    _quoteType = quoteType;
    _quoteUnits = quoteUnits;
  }
  
  /**
   * Creates an instance.
   * 
   * @param target  the target
   * @param name  the name
   * @param instrumentType  the instrument type
   * @param quoteType the quote type
   * @param quoteUnits the quote units
   * @return the volatility surface key, not null
   */
  public static VolatilitySurfaceKey of(final UniqueIdentifiable target, final String name, final String instrumentType, final String quoteType, final String quoteUnits) {
    ArgumentChecker.notNull(target, "target");
    return new VolatilitySurfaceKey(target, name, instrumentType, quoteType, quoteUnits);
  }

  //-------------------------------------------------------------------------
  /**
   * Compares this key to another, by currency then name.
   * 
   * @param other  the other key, not null
   * @return the comparison value
   */
  @Override
  public int compareTo(VolatilitySurfaceKey other) {
    if (other == null) {
      throw new NullPointerException();
    }
    int i = _target.compareTo(other.getTarget());
    if (i != 0) {
      return i;
    }
    i = ObjectUtils.compare(_name, other._name);
    if (i != 0) {
      return i;
    }
    i = ObjectUtils.compare(_instrumentType, other._instrumentType);
    if (i != 0) {
      return i;
    }
    i = ObjectUtils.compare(_quoteType, other._quoteType);
    if (i != 0) {
      return i;
    }
    return ObjectUtils.compare(_quoteUnits, other._quoteUnits);
  }

  @Override
  public <T> T accept(final Visitor<T> visitor) {
    return visitor.visitVolatilitySurfaceKey(this);
  }

  public MutableFudgeMsg toFudgeMsg(final FudgeSerializer serializer) {
    final MutableFudgeMsg msg = serializer.newMessage();
    msg.add("target", _target.toString());
    msg.add("name", _name);
    msg.add("instrumentType", _instrumentType);
    msg.add("quoteType", _quoteType);
    msg.add("quoteUnits", _quoteUnits);
    return msg;
  }

  public static VolatilitySurfaceKey fromFudgeMsg(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    final UniqueId targetUid;
    String target = msg.getString("target");
    if (target == null) {
      //Handle old form of snapshot
      Currency curr = Currency.of(msg.getString("currency"));
      targetUid = curr.getUniqueId();
    } else {
      targetUid = UniqueId.parse(target);
    }
    return new VolatilitySurfaceKey(targetUid, msg.getString("name"), msg.getString("instrumentType"), msg.getString("quoteType"), msg.getString("quoteUnits"));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code VolatilitySurfaceKey}.
   * @return the meta-bean, not null
   */
  public static VolatilitySurfaceKey.Meta meta() {
    return VolatilitySurfaceKey.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(VolatilitySurfaceKey.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   *
   * @return the builder, not null
   */
  public static VolatilitySurfaceKey.Builder builder() {
    return new VolatilitySurfaceKey.Builder();
  }

  @Override
  public VolatilitySurfaceKey.Meta metaBean() {
    return VolatilitySurfaceKey.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the target.
   * @return the value of the property
   */
  public UniqueId getTarget() {
    return _target;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the curve name.
   * @return the value of the property
   */
  public String getName() {
    return _name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the instrument type.
   * @return the value of the property
   */
  public String getInstrumentType() {
    return _instrumentType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the quote type.
   * @return the value of the property
   */
  public String getQuoteType() {
    return _quoteType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the quote units.
   * @return the value of the property
   */
  public String getQuoteUnits() {
    return _quoteUnits;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public VolatilitySurfaceKey clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      VolatilitySurfaceKey other = (VolatilitySurfaceKey) obj;
      return JodaBeanUtils.equal(getTarget(), other.getTarget()) &&
          JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getInstrumentType(), other.getInstrumentType()) &&
          JodaBeanUtils.equal(getQuoteType(), other.getQuoteType()) &&
          JodaBeanUtils.equal(getQuoteUnits(), other.getQuoteUnits());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getTarget());
    hash += hash * 31 + JodaBeanUtils.hashCode(getName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getInstrumentType());
    hash += hash * 31 + JodaBeanUtils.hashCode(getQuoteType());
    hash += hash * 31 + JodaBeanUtils.hashCode(getQuoteUnits());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("VolatilitySurfaceKey{");
    buf.append("target").append('=').append(getTarget()).append(',').append(' ');
    buf.append("name").append('=').append(getName()).append(',').append(' ');
    buf.append("instrumentType").append('=').append(getInstrumentType()).append(',').append(' ');
    buf.append("quoteType").append('=').append(getQuoteType()).append(',').append(' ');
    buf.append("quoteUnits").append('=').append(getQuoteUnits());
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code VolatilitySurfaceKey}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code target} property.
     */
    private final MetaProperty<UniqueId> _target = DirectMetaProperty.ofImmutable(
        this, "target", VolatilitySurfaceKey.class, UniqueId.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofImmutable(
        this, "name", VolatilitySurfaceKey.class, String.class);
    /**
     * The meta-property for the {@code instrumentType} property.
     */
    private final MetaProperty<String> _instrumentType = DirectMetaProperty.ofImmutable(
        this, "instrumentType", VolatilitySurfaceKey.class, String.class);
    /**
     * The meta-property for the {@code quoteType} property.
     */
    private final MetaProperty<String> _quoteType = DirectMetaProperty.ofImmutable(
        this, "quoteType", VolatilitySurfaceKey.class, String.class);
    /**
     * The meta-property for the {@code quoteUnits} property.
     */
    private final MetaProperty<String> _quoteUnits = DirectMetaProperty.ofImmutable(
        this, "quoteUnits", VolatilitySurfaceKey.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "target",
        "name",
        "instrumentType",
        "quoteType",
        "quoteUnits");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -880905839:  // target
          return _target;
        case 3373707:  // name
          return _name;
        case 1956846529:  // instrumentType
          return _instrumentType;
        case -1482972202:  // quoteType
          return _quoteType;
        case 1273091667:  // quoteUnits
          return _quoteUnits;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public VolatilitySurfaceKey.Builder builder() {
      return new VolatilitySurfaceKey.Builder();
    }

    @Override
    public Class<? extends VolatilitySurfaceKey> beanType() {
      return VolatilitySurfaceKey.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code target} property.
     * @return the meta-property, not null
     */
    public MetaProperty<UniqueId> target() {
      return _target;
    }

    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code instrumentType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> instrumentType() {
      return _instrumentType;
    }

    /**
     * The meta-property for the {@code quoteType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> quoteType() {
      return _quoteType;
    }

    /**
     * The meta-property for the {@code quoteUnits} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> quoteUnits() {
      return _quoteUnits;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -880905839:  // target
          return ((VolatilitySurfaceKey) bean).getTarget();
        case 3373707:  // name
          return ((VolatilitySurfaceKey) bean).getName();
        case 1956846529:  // instrumentType
          return ((VolatilitySurfaceKey) bean).getInstrumentType();
        case -1482972202:  // quoteType
          return ((VolatilitySurfaceKey) bean).getQuoteType();
        case 1273091667:  // quoteUnits
          return ((VolatilitySurfaceKey) bean).getQuoteUnits();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code VolatilitySurfaceKey}.
   */
  public static final class Builder extends BasicImmutableBeanBuilder<VolatilitySurfaceKey> {

    private UniqueId _target;
    private String _name;
    private String _instrumentType;
    private String _quoteType;
    private String _quoteUnits;

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(VolatilitySurfaceKey.Meta.INSTANCE);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(VolatilitySurfaceKey beanToCopy) {
      super(VolatilitySurfaceKey.Meta.INSTANCE);
      this._target = beanToCopy.getTarget();
      this._name = beanToCopy.getName();
      this._instrumentType = beanToCopy.getInstrumentType();
      this._quoteType = beanToCopy.getQuoteType();
      this._quoteUnits = beanToCopy.getQuoteUnits();
    }

    //-----------------------------------------------------------------------
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -880905839:  // target
          this._target = (UniqueId) newValue;
          break;
        case 3373707:  // name
          this._name = (String) newValue;
          break;
        case 1956846529:  // instrumentType
          this._instrumentType = (String) newValue;
          break;
        case -1482972202:  // quoteType
          this._quoteType = (String) newValue;
          break;
        case 1273091667:  // quoteUnits
          this._quoteUnits = (String) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public VolatilitySurfaceKey build() {
      return new VolatilitySurfaceKey(
          _target,
          _name,
          _instrumentType,
          _quoteType,
          _quoteUnits);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code target} property in the builder.
     * @param target  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder target(UniqueId target) {
      this._target = target;
      return this;
    }

    /**
     * Sets the {@code name} property in the builder.
     * @param name  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder name(String name) {
      this._name = name;
      return this;
    }

    /**
     * Sets the {@code instrumentType} property in the builder.
     * @param instrumentType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder instrumentType(String instrumentType) {
      this._instrumentType = instrumentType;
      return this;
    }

    /**
     * Sets the {@code quoteType} property in the builder.
     * @param quoteType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder quoteType(String quoteType) {
      this._quoteType = quoteType;
      return this;
    }

    /**
     * Sets the {@code quoteUnits} property in the builder.
     * @param quoteUnits  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder quoteUnits(String quoteUnits) {
      this._quoteUnits = quoteUnits;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(192);
      buf.append("VolatilitySurfaceKey.Builder{");
      buf.append("target").append('=').append(_target).append(',').append(' ');
      buf.append("name").append('=').append(_name).append(',').append(' ');
      buf.append("instrumentType").append('=').append(_instrumentType).append(',').append(' ');
      buf.append("quoteType").append('=').append(_quoteType).append(',').append(' ');
      buf.append("quoteUnits").append('=').append(_quoteUnits);
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
