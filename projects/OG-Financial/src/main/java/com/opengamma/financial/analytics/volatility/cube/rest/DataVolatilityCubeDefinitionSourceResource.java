/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.volatility.cube.rest;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.threeten.bp.Instant;

import com.opengamma.financial.analytics.volatility.cube.VolatilityCubeDefinition;
import com.opengamma.financial.analytics.volatility.cube.VolatilityCubeDefinitionSource;
import com.opengamma.id.VersionCorrection;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import com.opengamma.util.rest.AbstractDataResource;

/**
 * RESTful resource for the volatility cube source.
 * <p>
 * This resource receives and processes RESTful calls to the source.
 */
@Path("volatilityCubeDefinitionSource")
public class DataVolatilityCubeDefinitionSourceResource extends AbstractDataResource {

  /**
   * The source.
   */
  private final VolatilityCubeDefinitionSource _source;

  /**
   * Creates the resource, exposing the underlying source over REST.
   * 
   * @param source  the underlying source, not null
   */
  public DataVolatilityCubeDefinitionSourceResource(final VolatilityCubeDefinitionSource source) {
    ArgumentChecker.notNull(source, "source");
    _source = source;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the source.
   * 
   * @return the source, not null
   */
  public VolatilityCubeDefinitionSource getVolatilityCubeDefinitionSource() {
    return _source;
  }

  //-------------------------------------------------------------------------
  @GET
  public Response getHateaos(@Context UriInfo uriInfo) {
    return hateoasResponse(uriInfo);
  }

  @GET
  @Path("definitions/searchSingle")
  public Response searchSingle(
      @QueryParam("currency") String currencyStr,
      @QueryParam("versionAsOf") String versionAsOfStr,
      @QueryParam("name") String name) {
    final Currency currency = Currency.parse(currencyStr);
    if (versionAsOfStr != null) {
      final VersionCorrection versionCorrection = VersionCorrection.parse(versionAsOfStr, null);
      VolatilityCubeDefinition result = getVolatilityCubeDefinitionSource().getDefinition(currency, name, versionCorrection);
      return responseOkFudge(result);
    } else {
      VolatilityCubeDefinition result = getVolatilityCubeDefinitionSource().getDefinition(currency, name);
      return responseOkFudge(result);
    }
  }

  /**
   * Builds a URI.
   * 
   * @param baseUri  the base URI, not null
   * @param currency  the currency, not null
   * @param name  the name, not null
   * @param versionAsOf  the version to fetch, null means latest
   * @return the URI, not null
   */
  public static URI uriSearchSingle(URI baseUri, Currency currency, String name, Instant versionAsOf) {
    UriBuilder bld = UriBuilder.fromUri(baseUri).path("/definitions/searchSingle");
    bld.queryParam("currency", currency.toString());
    bld.queryParam("name", name);
    if (versionAsOf != null) {
      bld.queryParam("versionAsOf", versionAsOf.toString());
    }
    return bld.build();
  }

}
