package org.expath.ns;

import static org.basex.util.Token.*;

import java.util.*;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;

import com.vividsolutions.jts.geom.*;

/**
 * This class contains functions to read gml elements.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Masoumeh Seydi
 */
public final class GmlReader {
  /** GML URI. */
  private static final byte[] GMLURI = token("http://www.opengis.net/gml");

  /** Prefix: "gml:". */
  private static final String GML = "gml:";
  /** QName gml:Point. */
  private static final QNm Q_GML_POINT = new QNm(GML + "Point", GMLURI);
  /** QName gml:LineString. */
  private static final QNm Q_GML_LINESTRING = new QNm(GML + "LineString", GMLURI);
  /** QName gml:LinearRing. */
  private static final QNm Q_GML_LINEARRING = new QNm(GML + "LinearRing", GMLURI);
  /** QName gml:Polygon. */
  private static final QNm Q_GML_POLYGON = new QNm(GML + "Polygon", GMLURI);
  /** QName gml:MultiPoint. */
  private static final QNm Q_GML_MULTIPOINT = new QNm(GML + "MultiPoint", GMLURI);
  /** QName gml:pointMember. */
  private static final QNm Q_GML_POINTMEMBER = new QNm(GML + "pointMember", GMLURI);
  /** QName gml:MultiLineString. */
  private static final QNm Q_GML_MULTILINESTRING =
      new QNm(GML + "MultiLineString", GMLURI);
  /** QName gml:lineStringMember. */
  private static final QNm Q_GML_LINESTRINGMEMBER =
      new QNm(GML + "lineStringMember", GMLURI);
  /** QName gml:MultiPolygon. */
  private static final QNm Q_GML_MULTIPOLYGON = new QNm(GML + "MultiPolygon", GMLURI);
  /** QName gml:polygonMember. */
  private static final QNm Q_GML_POLYGONMEMBER = new QNm(GML + "polygonMember", GMLURI);
  /** QName gml:MultiGeometry. */
  private static final QNm Q_GML_MULTIGEOMETRY = new QNm(GML + "MultiGeometry", GMLURI);
  /** QName gml:geometryMember. */
  private static final QNm Q_GML_GEOMETRYMEMBER = new QNm(GML + "geometryMember", GMLURI);
  /** QName gml:outerBoundaryIs. */
  private static final QNm Q_GML_OUTERBOUNDARY = new QNm(GML + "outerBoundaryIs", GMLURI);
  /** QName gml:innerBoundaryIs. */
  private static final QNm Q_GML_INNERBOUNDARY = new QNm(GML + "innerBoundaryIs", GMLURI);
  /** QName gml:coordinates. */
  private static final QNm Q_GML_COORDINATES = new QNm(GML + "coordinates", GMLURI);
  /** QName coord. */
  private static final QNm Q_GML_COORD = new QNm(GML + "coord", GMLURI);
  /** QName X. */
  private static final QNm Q_GML_X = new QNm(GML + "X", GMLURI);
  /** QName Y. */
  private static final QNm Q_GML_Y = new QNm(GML + "Y", GMLURI);
  /** QName Z. */
  private static final QNm Q_GML_Z = new QNm(GML + "Z", GMLURI);

  /** Array containing single geometries' QName. */
  private static final QNm[] QNAMES = { Q_GML_POINT, Q_GML_LINESTRING, Q_GML_POLYGON };

  /** JTS GeometryFactory. */
  private final GeometryFactory gFactory = new GeometryFactory();
  /** JTS CoordinateSequenceFactory. */
  private final CoordinateSequenceFactory csFactory =
      gFactory.getCoordinateSequenceFactory();

  // PUBLIC METHODS =====================================================================

  /**
   * Creates a coordinate array out of a <gml:coordinates> element.
   * @param pattern coordinate string of the text value in <gml:coordinates> element
   * @return coordinates of the points included in <gml:coordinates> element
   * @throws QueryException query exception
   */
  public Coordinate[] createCoordinate(final byte[] pattern) throws QueryException {
    final List<Coordinate> coord = new ArrayList<Coordinate>();
    for(final byte[] coords : split(norm(pattern), ' ')) {
      final byte[][] xy = split(coords, ',');
      //if(xy.length != 2) throw GeoErrors.invalidCoordErr();
      if (xy.length == 1)
        coord.add(new Coordinate(toDouble(trim(xy[0])), 0, Double.NaN));
      else if (xy.length == 2)
        coord.add(new Coordinate(toDouble(trim(xy[0])), toDouble(trim(xy[1])),
            Double.NaN));
      else if (xy.length == 3)
        coord.add(new Coordinate(toDouble(trim(xy[0])), toDouble(trim(xy[1])),
          toDouble(trim(xy[2]))));
      else throw GeoErrors.invalidCoordErr();
    }
    if(!coord.isEmpty()) return coord.toArray(new Coordinate[coord.size()]);
    throw GeoErrors.invalidCoordErr();
  }

  /**
   * Reads an <gml:Polygon> element and returns a Polygon geometry.
   * @param node Geometry element node
   * @return Polygon geometry out of the node element
   * @throws QueryException query exception
   */
  public Polygon createPolygon(final ANode node) throws QueryException {
    LinearRing shell = null;
    final ArrayList<LinearRing> ir = new ArrayList<LinearRing>();
    for(final ANode ch : node.children()) {
      if(ch.type != NodeType.ELM) continue;
      final QNm name = ch.qname();

      if(name.eq(Q_GML_OUTERBOUNDARY)) {
        final ANode c = ch.children().next();
        if(c == null || !c.qname().eq(Q_GML_LINEARRING)) throw GeoErrors.geoAssrErr(node);
        if(shell != null) throw GeoErrors.outRingErr(node);
        shell = createRing(c);
      } else if(name.eq(Q_GML_INNERBOUNDARY)) {
        final ANode c = ch.children().next();
        if(c == null || !c.qname().eq(Q_GML_LINEARRING)) throw GeoErrors.geoAssrErr(node);
        ir.add(createRing(c));
      } else {
        throw GeoErrors.geoAssrErr(node);
      }
    }
    if(!ir.isEmpty() && shell == null) throw GeoErrors.outRingErr(node);
    return new Polygon(shell, ir.toArray(new LinearRing[ir.size()]), gFactory);
  }

  /**
   * Reads a <gml:LineString> element and returns a LineString geometry.
   * @param node Geometry element node
   * @return LineString geometry out of the node element
   * @throws QueryException query exception
   */
  public LineString createLineString(final ANode node) throws QueryException {
    try {
      return new LineString(csFactory.create(createCoordSeq(node)), gFactory);
    } catch(final RuntimeException ex) {
      // catches IllegalAccessExceptions and AssertionFailedException
      throw GeoErrors.jtsConstruction(ex);
    }
  }

  /**
   * Reads a <gml:Point> element and returns a Point geometry.
   * @param node Geometry element node
   * @return Point geometry out of the node element
   * @throws QueryException query exception
   */
  public Point createPoint(final ANode node) throws QueryException {
    try {
      return new Point(csFactory.create(createCoordSeq(node)), gFactory);
    } catch(final RuntimeException ex) {
      // catches IllegalAccessExceptions and AssertionFailedException
      throw GeoErrors.jtsConstruction(ex);
    }
  }

  /**
   * Reads an element of type GeometryCollection and creates
   * the corresponding GeometryCollection geometry.
   * @param node GeometryCollection element node
   * @param mQnm one of GeometryCollection qnames
   * @param sQnm one or more qname of single Geometries
   * @return Corresponding GeometryCollection
   * @throws QueryException query exception
   */
  public GeometryCollection createMGeom(final ANode node, final QNm mQnm,
      final QNm... sQnm) throws QueryException {

    final ArrayList<Geometry> gList = new ArrayList<Geometry>();
    for(final ANode ch : node.children()) {
      for(final QNm name : sQnm) {
        if(ch.qname().eq(name)) {
          gList.add(createGeometry(ch));
        } else if(ch.qname().eq(mQnm)) {
          final ANode c = ch.children().next();
          if(c == null) throw GeoErrors.geoAssrErr(node);
          gList.add(createGeometry(c));
        }
      }
    }
    if(node.qname().eq(Q_GML_MULTILINESTRING))
      return new MultiLineString(gList.toArray(new LineString[gList.size()]), gFactory);
    else if(node.qname().eq(Q_GML_MULTIPOINT))
      return new MultiPoint(gList.toArray(new Point[gList.size()]), gFactory);
    else if(node.qname().eq(Q_GML_MULTIPOLYGON))
      return new MultiPolygon(gList.toArray(new Polygon[gList.size()]), gFactory);
    return new GeometryCollection(gList.toArray(new Geometry[gList.size()]), gFactory);
  }

  /**
   * Reads a gml element and calls the appropriate function to create the geometry.
   * @param node Geometry element node
   * @return Geometry out of the element
   * @throws QueryException query exception
   */
  public Geometry createGeometry(final ANode node) throws QueryException {
    final QNm name = node.qname();
    //Performance p = new Performance();
    //long read = 0;

    try {
      if(name.eq(Q_GML_POLYGON)) {
        Geometry geo = createPolygon(node);
//        read += p.time();
//        System.out.println("create poly: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_LINESTRING)) {
        Geometry geo = createLineString(node);
//        read += p.time();
//        System.out.println("create line: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_LINEARRING)) {
        Geometry geo = createRing(node);
//        read += p.time();
//        System.out.println("create ring: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_POINT)) {
        Geometry geo = createPoint(node);
//        read += p.time();
//        System.out.println("create point: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_MULTIPOLYGON)) {
        Geometry geo = createMGeom(node, Q_GML_POLYGONMEMBER, Q_GML_POLYGON);
//        read += p.time();
//        System.out.println("create Mpoly: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_MULTILINESTRING)) {
        Geometry geo = createMGeom(node, Q_GML_LINESTRINGMEMBER, Q_GML_LINESTRING);
//        read += p.time();
//        System.out.println("create Mline: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_MULTIPOINT)) {
        Geometry geo = createMGeom(node, Q_GML_POINTMEMBER, Q_GML_POINT);
//        read += p.time();
//        System.out.println("create MPoint: " + Performance.getTime(read, 1));
        return geo;
      }
      if(name.eq(Q_GML_MULTIGEOMETRY)) {
        Geometry geo = createMGeom(node, Q_GML_GEOMETRYMEMBER, QNAMES);
//        read += p.time();
//        System.out.println("create Mgeom: " + Performance.getTime(read, 1));
        return geo;
      }
    } catch (final QueryException e) {
        throw e;
    }

    throw GeoErrors.unrecognizedGeo(node);
  }

  // PRIVATE METHODS ====================================================================

  /**
   * Reads an <gml:LinearRing> element and returns a LinearRing geometry.
   * @param n The node to be processed
   * @return LinearRing geometry
   * @throws QueryException query exception
   */
  private LinearRing createRing(final ANode n) throws QueryException {
    try {
      return new LinearRing(csFactory.create(createCoordSeq(n)), gFactory);
    } catch(final RuntimeException ex) {
      // catches IllegalAccessExceptions and AssertionFailedException
      throw GeoErrors.jtsConstruction(ex);
    }
  }

  /**
   * Creates coordinates sequence out of <gml:coordinate> element
   * or a set of <gml:coord> elements.
   * @param node Geometry element node
   * @return Coordinate sequence
   * @throws QueryException query exception
   */
  private Coordinate[] createCoordSeq(final ANode node) throws QueryException {
    final List<Coordinate> co = new ArrayList<Coordinate>();
    for(final ANode ch : node.children()) {
      if(ch.type != NodeType.ELM) continue;

      final QNm name = ch.qname();
      if(name.eq(Q_GML_COORDINATES)) {
        return createCoordinate(ch.string());
      } else if(name.eq(Q_GML_COORD)) {
        co.add(createCoord(ch));
      } else {
        throw GeoErrors.geoAssrErr(node);
      }
    }
    return co.toArray(new Coordinate[co.size()]);
  }

  /**
   * Creates coordinate sequence out of <gml:coord> element.
   * @param coord <gml:coord> element to be processed
   * @return coordinate
   * @throws QueryException query exception
   */
  private Coordinate createCoord(final ANode coord) throws QueryException {
    double x = Double.NaN, y = 0, z = 0;
    for(final ANode ch : coord.children()) {
      if(ch.type != NodeType.ELM) continue;

      final double d = toDouble(trim(ch.string()));
      if(Double.isNaN(d)) throw GeoErrors.invalidCoordErr();

      final QNm name = ch.qname();
      if(name.eq(Q_GML_X)) x = d;
      else if(name.eq(Q_GML_Y)) y = d;
      else if(name.eq(Q_GML_Z)) z = d;
      else throw GeoErrors.invalidCoordErr();
    }
    if(Double.isNaN(x)) throw GeoErrors.invalidCoordErr();
    return new Coordinate(x, y, z);
  }
}

