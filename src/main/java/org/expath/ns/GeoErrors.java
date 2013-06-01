package org.expath.ns;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * This module contains static error functions for the Geo module.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Masoumeh Seydi
 */
public final class GeoErrors {
  /** Error namespace. */
  private static final byte[] NS = QueryText.EXPERROR;
  /** Namespace and error code prefix. */
  private static final String PREFIX =
      new TokenBuilder(QueryText.EXPERR).add(":GEO").toString();

  /** Private constructor, preventing instantiation. */
  private GeoErrors() { }

  /**
   * GEO0001: Unrecognized geo object.
   * @param element Geometry object
   * @return query exception
   */
  static QueryException unrecognizedGeo(final Object element) {
    return thrw(1, "Unrecognized Geo type: %", element);
  }

  /**
   * GEO0002: gml reader error massage (JTS).
   * @param e error
   * @return query exception
   */
  static QueryException gmlReaderErr(final Object e) {
    return thrw(2, "%", e);
  }

  /**
   * GEO0003: Inappropriate input geometry.
   * @param input Geometry object
   * @param geo exact Geometry object
   * @return query exception
   */
  static QueryException geoType(final Object input, final String geo) {
    return thrw(3, "% is not an appropiate geometry for this function. "
              + "The input geometry should be a %.", input, geo);
  }

  /**
   * GEO0004: Out of range index.
   * @param geoNumber index
   * @return query exception
   */
  static QueryException outOfRangeIdx(final Int geoNumber) {
    return thrw(4, "Out of range input index: %", geoNumber);
  }

  /**
   * GEO0005: gmlWriter Error(JTS).
   * @param e error
   * @return query exception
   */
  static QueryException gmlWriterErr(final Object e) {
    return thrw(5, "%", e);
  }

  /**
   * GEO0006: Geometry assertion failed.
   * @param e error
   * @return query exception
   */
  static QueryException geoAssrErr(final Object e) {
    return thrw(6, "Geometry (%) assertion failed", e);
  }

  /**
   * GEO0007: Invalid number of outer boundary.
   * @param e error
   * @return query exception
   */
  static QueryException outRingErr(final Object e) {
    return thrw(7, "Invalid number of ouetr boundary in %.", e);
  }


//  /**
//   * GEO0008: Invalid number of points.
//   * @param e error
//   * @return query exception
//   */
//  static QueryException pointsNumErr(final Object e) {
//    return thrw(8, "Invalid number of points in %.", e);
//  }
//
//  /**
//   * GEO0009: Open LinearRing.
//   * @return query exception
//   */
//  static QueryException openRingErr() {
//    return thrw(9, "The LinearRing is not a closed line.");
//  }

/**
* GEO0008: Illegal access exception or assertion failure error.
* @param e error
* @return query exception
*/
static QueryException jtsConstruction(final Object e) {
 return thrw(8, "Assertion faild: %.", e);
}

  /**
   * GEO0009: Empty coordinate.
   * @return query exception
   */
  static QueryException invalidCoordErr() {
    return thrw(9, "Invalid coordinate found.");
  }

  /**
   * Returns a query exception.
   * @param code code
   * @param msg message
   * @param ext extension
   * @return query exception
   */
  private static QueryException thrw(final int code, final String msg,
      final Object... ext) {
    return new QueryException(null, qname(code), msg, ext);
  }

  /**
   * Creates an error QName for the specified code.
   * @param code code
   * @return query exception
   */
  public static QNm qname(final int code) {
    return new QNm(String.format("%s:GEO%04d", PREFIX, code), NS);
  }
}
