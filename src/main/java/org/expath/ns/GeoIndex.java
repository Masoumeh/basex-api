package org.expath.ns;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

import org.basex.*;
import org.basex.data.*;
import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
import org.basex.query.value.node.*;
import org.basex.util.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.gml2.GMLReader;

/**
 * This class contains the functions implemented by STRtree index.
 * @author Masoumeh Seydi
 *
 * Additional spatial function using STRtree index.
 */
public class GeoIndex extends QueryModule {
  /** Tree hash map. */
  static HashMap<String, STRtree> trees = new HashMap<String, STRtree>();
  /** Data. */
  Data data;
  /** Basex custom GML Reader. */
  final GmlReader bxGmlReader = new GmlReader();

  /**
   * Reads an index file into a STRtree.
   * @param db Database file name
   * @return STRtree STRtree out of the index file of the database
   * @throws Exception exception
   */
  STRtree readSTRtree(final String db) throws Exception {
    STRtree tree = trees.get(db);
    data = context.resource.data(db, null);
    if(tree == null) {
      File file = data.meta.dbfile("STRTreeIndex").file();

      ObjectInputStream ois = new ObjectInputStream(
          new BufferedInputStream(new FileInputStream(file)));
      tree = (STRtree) ois.readObject();
      trees.put(db.toString(), tree);
      ois.close();
    }
    return tree;
  }

  /**
   * Finds the specific geometry objects which their bound intersect
   * with the given geometry.
   * @param tree Index tree
   * @param geo Geometry that its bound is checked to find the other geometries
   * @return List of geometries
   * @throws Exception 
   */

//  public List<DBNode> visitor(final STRtree tree, final Geometry geo) {
//    GeoItemVisitor visitor = new GeoItemVisitor(data);
//    tree.query(geo.getEnvelopeInternal(), visitor);
//    return visitor.getList();
//  }
//  public /*List<DBNode>*/ Value visitor(final String db, final ANode geo) throws Exception {
//    STRtree tree = readSTRtree(db);
  public List<DBNode> visitor(final STRtree tree, final Geometry geo) throws Exception {
    GeoItemVisitor visitor = new GeoItemVisitor(data);
    tree.query(/*(bxGmlReader.createGeometry(geo))*/geo.getEnvelopeInternal(), visitor);
    return visitor.getList();
  }
  /**
   * Finds the two nearest neighbor in an index tree (STRtree).
   * @param db Database file name
   * @return Two nearest neighbor
   * @throws Exception exception
   */
  public Result nearestNeighbour(final String db) throws Exception {
    STRtree tree = readSTRtree(db);
    ValueBuilder vb = new ValueBuilder();
    Object[] ret = tree.nearestNeighbour(new GeoItemDistance(data));
    vb.add(new DBNode(data, (Integer) ret[0]));
    vb.add(new DBNode(data, (Integer) ret[1]));
    return vb;
  }
  /**
   * Finds the item in index tree (STRtree) which is the nearest to the given object.
   * @param db Database file name
   * @param obj Object
   * @return The nearest item to the Object
   * @throws Exception exception
   */
  public Result nearestNeighbour(final String db, final ANode obj) throws Exception {
    STRtree tree = readSTRtree(db);
    ValueBuilder vb = new ValueBuilder();
    final GMLReader gmlReader = new GMLReader();
    final GeometryFactory geoFactory = new GeometryFactory();
    Geometry geo = gmlReader.read(obj.serialize().toString(), geoFactory);
    Object ret = tree.nearestNeighbour(geo.getEnvelopeInternal(),
        ((DBNode) obj).pre, new GeoItemDistance(data));
    vb.add(new DBNode(data, (Integer) ret));
    return vb;
  }

  /**
   * Finds the items whose bound intersect with the given envelope.
   * @param db Database file name.
   * @param obj Object that its envelope is checked to find the others
   * @return List of items
   * @throws Exception exception
   */
  public Result query(final String db, final ANode obj) throws Exception {
    STRtree tree = readSTRtree(db);
    ValueBuilder vb = new ValueBuilder();
    Geometry geo = bxGmlReader.createGeometry(obj);
    List ret = tree.query(geo.getEnvelopeInternal());
    for(Object o : ret)
    vb.add(new DBNode(data, (Integer) o));
    return vb;
  }

  /**
   * Return all the geometries in a database which
   * the specified geometry, obj contains them.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database
   * @throws Exception exception
   */
//  public Result contains(final String db, final ANode obj) throws Exception {
//    STRtree tree = readSTRtree(db);
//    ValueBuilder vb = new ValueBuilder();
//    Geometry geo = bxGmlReader.createGeometry(obj);
//    List<DBNode> ret = visitor(tree, geo);
//    Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = bxGmlReader.createGeometry(dbn);
//      if (geo.contains(temp))
//        vb.add(dbn);
//    }
//    return vb;
//  }

  /**
   * Return all the geometries in a database which the specified geometry, obj
   * is within them.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database
   * @throws Exception exception
   */
//  public Result within(final String db, final ANode obj) throws Exception {
//    ValueBuilder vb = new ValueBuilder();
//
//    STRtree tree = readSTRtree(db);
//    Geometry geo = bxGmlReader.createGeometry(obj);
//    List<DBNode> ret = visitor(tree, geo);
//    Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = bxGmlReader.createGeometry(obj);
//      if (temp.within(geo))
//        vb.add(dbn);
//    }
//    return vb;
//  }


  /**
   * Return all the geometries in a database which intersect
   * with the specified geometry, obj.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database.
   * @throws Exception exception
   */
  public Result intersects(final String db, final ANode obj) throws Exception {

    STRtree tree = readSTRtree(db);
    ValueBuilder vb = new ValueBuilder();
    long sRead = 0;
    long read = 0;
    long test = 0;
    long visit = 0;
    Performance p = new Performance();
    //System.out.println("geo1: ");
    Geometry geo = bxGmlReader.createGeometry(obj);
    sRead += p.time();
    //System.out.println("input geo: " + geo);
    List<DBNode> ret = visitor(tree, geo);
//    Value ret = visitor(db, obj);
    //long visitNr = ret.size();
    visit += p.time();
    Geometry temp = null;
    for(DBNode dbn : ret) {
   //  for (Value dbn : ret) {
     // System.out.println("geo2: ");
       temp = bxGmlReader.createGeometry((DBNode) dbn);
      read += p.time();
      if (geo.intersects(temp)) {
        test += p.time();
        vb.add(dbn);
      }
    }
    //System.out.println("visit numbers: " + visitNr);
    System.out.println("sRead: " + Performance.getTime(sRead, 1));
    System.out.println("visit: " + Performance.getTime(visit, 1));
    System.out.println("read: " + Performance.getTime(read, 1));
    System.out.println("test: " + Performance.getTime(test, 1));
    return vb;
  }

  /**
   * Return all the geometries in a database which
   * the specified geometry, obj touches them.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database
   * @throws Exception exception
   */
//  public Result touches(final String db, final ANode obj) throws Exception { ///////////
//    STRtree tree = readSTRtree(db);
//    ValueBuilder vb = new ValueBuilder();
//    Geometry geo = bxGmlReader.createGeometry(obj);
//
//    List<DBNode> ret = visitor(tree, geo);
//        Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = new GmlReader().createGeometry(dbn);
//      if (geo.touches(temp))
//        vb.add(dbn);
//    }
//    return vb;
//  }

  /**
   * Return all the geometries in a database which
   * the specified geometry, obj, is equal with them.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database
   * @throws Exception exception
   */
//  public Result equals(final String db, final ANode obj) throws Exception { ////////////
//
//    STRtree tree = readSTRtree(db);
//    ValueBuilder vb = new ValueBuilder();
//    Geometry geo = bxGmlReader.createGeometry(obj);
//
//    List<DBNode> ret = visitor(tree, geo);
//
//    Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = bxGmlReader.createGeometry(dbn);
//      if (geo.equals(temp))
//        vb.add(dbn);
//    }
//    return vb;
//  }

  /**
   * Return all the geometries in a database which
   * the specified geometry, obj, overlaps them.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database
   * @throws Exception exception
   */
//  public Result overlaps(final String db, final ANode obj) throws Exception {
//
//    STRtree tree = readSTRtree(db);
//    ValueBuilder vb = new ValueBuilder();
//    Geometry geo = bxGmlReader.createGeometry(obj);
//
//    List<DBNode> ret = visitor(tree, geo);
//
//    Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = bxGmlReader.createGeometry(dbn);
//      if (geo.overlaps(temp))
//        vb.add(dbn);
//    }
//    return vb;
//  }

  /**
   * Return all the geometries in a database which
   * the specified geometry, obj, crosses them.
   * @param db Database file name
   * @param obj Geo object which is checked against the database node set
   * @return Set of geo objects in the database
   * @throws Exception exception
   */
//  public Result crosses(final String db, final ANode obj) throws Exception {
//    STRtree tree = readSTRtree(db);
//    ValueBuilder vb = new ValueBuilder();
//    Geometry geo = bxGmlReader.createGeometry(obj);
//
//    List<DBNode> ret = visitor(tree, geo);
//    Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = bxGmlReader.createGeometry(dbn);
//      if (geo.crosses(temp))
//        vb.add(dbn);
//    }
//    return vb;
//  }
//
//  /**
//   * Return all the geometries in a database which
//   * the specified geometry, obj, covers them.
//   * @param db Database file name
//   * @param obj Geo object which is checked against the database node set
//   * @return Set of geo objects in the database
//   * @throws Exception exception
//   */
//  public Result covers(final String db, final ANode obj) throws Exception {
//
//    STRtree tree = readSTRtree(db);
//    ValueBuilder vb = new ValueBuilder();
//    Geometry geo = bxGmlReader.createGeometry(obj);
//
//    List<DBNode> ret = visitor(tree, geo);
//
//    Geometry temp = null;
//    for(DBNode dbn : ret) {
//      temp = bxGmlReader.createGeometry(dbn);
//      if (geo.covers(temp))
//        vb.add(dbn);
//    }
//    return vb;
//  }
  public static void main (String[] args) throws Exception {
    new BaseXGUI();
  }
}
