/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.master;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.google.common.collect.Sets;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;

/**
 * A position held in a position master portfolio.
 * <p>
 * Positions are logically attached to nodes in the portfolio tree, however they are
 * stored and returned separately from the position master.
 */
@BeanDefinition
public class ManageablePosition extends DirectBean implements MutableUniqueIdentifiable {

  /**
   * The position unique identifier.
   */
  @PropertyDefinition
  private UniqueIdentifier _uniqueIdentifier;
  /**
   * The position name.
   */
  @PropertyDefinition(get = "manual", set = "")
  @SuppressWarnings("unused")
  private String _name = "";
  /**
   * The quantity.
   */
  @PropertyDefinition
  private BigDecimal _quantity;
  /**
   * The identifiers specifying the security.
   */
  @PropertyDefinition
  private IdentifierBundle _securityKey;
  /**
   * The trades
   */
  @PropertyDefinition
  private Set<ManageableTrade> _trades = Sets.newHashSet();

  /**
   * Creates an instance.
   */
  public ManageablePosition() {
  }

  /**
   * Creates a position from an amount of a security identified by key.
   * @param quantity  the amount of the position, not null
   * @param securityKey  the security identifier, not null
   */
  public ManageablePosition(final BigDecimal quantity, final Identifier securityKey) {
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(securityKey, "securityKey");
    _quantity = quantity;
    _securityKey = IdentifierBundle.of(securityKey);
  }

  /**
   * Creates a position from an amount of a security identified by key.
   * @param quantity  the amount of the position, not null
   * @param securityKey  the security identifier, not null
   */
  public ManageablePosition(final BigDecimal quantity, final IdentifierBundle securityKey) {
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(securityKey, "securityKey");
    _quantity = quantity;
    _securityKey = securityKey;
  }

  //-------------------------------------------------------------------------
  /**
   * Adds an identifier to the security key.
   * @param securityKeyIdentifier  the identifier to add, not null
   */
  public void addSecurityKey(final Identifier securityKeyIdentifier) {
    ArgumentChecker.notNull(securityKeyIdentifier, "securityKeyIdentifier");
    setSecurityKey(getSecurityKey().withIdentifier(securityKeyIdentifier));
  }

  /**
   * Gets a suitable name for the position.
   * @return the name, not null
   */
  public String getName() {
    return getUniqueIdentifier() != null ? getUniqueIdentifier().toLatest().toString() : "";
  }
  
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ManageablePosition}.
   * @return the meta-bean, not null
   */
  public static ManageablePosition.Meta meta() {
    return ManageablePosition.Meta.INSTANCE;
  }

  @Override
  public ManageablePosition.Meta metaBean() {
    return ManageablePosition.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -125484198:  // uniqueIdentifier
        return getUniqueIdentifier();
      case 3373707:  // name
        return getName();
      case -1285004149:  // quantity
        return getQuantity();
      case 1550083839:  // securityKey
        return getSecurityKey();
      case -865715313:  // trades
        return getTrades();
    }
    return super.propertyGet(propertyName);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -125484198:  // uniqueIdentifier
        setUniqueIdentifier((UniqueIdentifier) newValue);
        return;
      case 3373707:  // name
        throw new UnsupportedOperationException("Property cannot be written: name");
      case -1285004149:  // quantity
        setQuantity((BigDecimal) newValue);
        return;
      case 1550083839:  // securityKey
        setSecurityKey((IdentifierBundle) newValue);
        return;
      case -865715313:  // trades
        setTrades((Set<ManageableTrade>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the position unique identifier.
   * @return the value of the property
   */
  public UniqueIdentifier getUniqueIdentifier() {
    return _uniqueIdentifier;
  }

  /**
   * Sets the position unique identifier.
   * @param uniqueIdentifier  the new value of the property
   */
  public void setUniqueIdentifier(UniqueIdentifier uniqueIdentifier) {
    this._uniqueIdentifier = uniqueIdentifier;
  }

  /**
   * Gets the the {@code uniqueIdentifier} property.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> uniqueIdentifier() {
    return metaBean().uniqueIdentifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the the {@code name} property.
   * @return the property, not null
   */
  public final Property<String> name() {
    return metaBean().name().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the quantity.
   * @return the value of the property
   */
  public BigDecimal getQuantity() {
    return _quantity;
  }

  /**
   * Sets the quantity.
   * @param quantity  the new value of the property
   */
  public void setQuantity(BigDecimal quantity) {
    this._quantity = quantity;
  }

  /**
   * Gets the the {@code quantity} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> quantity() {
    return metaBean().quantity().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identifiers specifying the security.
   * @return the value of the property
   */
  public IdentifierBundle getSecurityKey() {
    return _securityKey;
  }

  /**
   * Sets the identifiers specifying the security.
   * @param securityKey  the new value of the property
   */
  public void setSecurityKey(IdentifierBundle securityKey) {
    this._securityKey = securityKey;
  }

  /**
   * Gets the the {@code securityKey} property.
   * @return the property, not null
   */
  public final Property<IdentifierBundle> securityKey() {
    return metaBean().securityKey().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trades
   * @return the value of the property
   */
  public Set<ManageableTrade> getTrades() {
    return _trades;
  }

  /**
   * Sets the trades
   * @param trades  the new value of the property
   */
  public void setTrades(Set<ManageableTrade> trades) {
    this._trades = trades;
  }

  /**
   * Gets the the {@code trades} property.
   * @return the property, not null
   */
  public final Property<Set<ManageableTrade>> trades() {
    return metaBean().trades().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ManageablePosition}.
   */
  public static class Meta extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueIdentifier} property.
     */
    private final MetaProperty<UniqueIdentifier> _uniqueIdentifier = DirectMetaProperty.ofReadWrite(this, "uniqueIdentifier", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadOnly(this, "name", String.class);
    /**
     * The meta-property for the {@code quantity} property.
     */
    private final MetaProperty<BigDecimal> _quantity = DirectMetaProperty.ofReadWrite(this, "quantity", BigDecimal.class);
    /**
     * The meta-property for the {@code securityKey} property.
     */
    private final MetaProperty<IdentifierBundle> _securityKey = DirectMetaProperty.ofReadWrite(this, "securityKey", IdentifierBundle.class);
    /**
     * The meta-property for the {@code trades} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<ManageableTrade>> _trades = DirectMetaProperty.ofReadWrite(this, "trades", (Class) Set.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("uniqueIdentifier", _uniqueIdentifier);
      temp.put("name", _name);
      temp.put("quantity", _quantity);
      temp.put("securityKey", _securityKey);
      temp.put("trades", _trades);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public ManageablePosition createBean() {
      return new ManageablePosition();
    }

    @Override
    public Class<? extends ManageablePosition> beanType() {
      return ManageablePosition.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueIdentifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> uniqueIdentifier() {
      return _uniqueIdentifier;
    }

    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code quantity} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> quantity() {
      return _quantity;
    }

    /**
     * The meta-property for the {@code securityKey} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<IdentifierBundle> securityKey() {
      return _securityKey;
    }

    /**
     * The meta-property for the {@code trades} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<ManageableTrade>> trades() {
      return _trades;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
