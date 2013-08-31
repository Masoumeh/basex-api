package org.expath.ns;

import java.io.*;
import java.util.*;

import org.basex.data.*;
import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
import org.basex.query.value.node.*;
import org.basex.util.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.strtree.*;
import com.vividsolutions.jts.io.gml2.*;


  /**
   * This class contains the functions implemented by STRtree index.
   * @author Masoumeh Seydi
   *
   * Additional spatial function using STRtree index.
   */
  public class SimpleGeoIndex extends QueryModule {
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
     * @param db Database name
     * @param geo Geometry that its bound is checked to find the other geometries
     * @return list of geometries
     * @throws Exception exception
     */
    public Value visitor(final String db, final ANode geo)
      throws Exception {
      long visitTime = 0;
      Performance p = new Performance();
      STRtree tree = readSTRtree(db);
      GeoItemVisitor visitor = new GeoItemVisitor(data);
      tree.query((bxGmlReader.createGeometry(geo)).getEnvelopeInternal(), visitor);
      visitTime += p.time();
//      System.out.println("Visit Time: " + Performance.getTime(visitTime, 1));
//      System.out.println("Visit Size:" + visitor.getList().size());
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
      List<?> ret = tree.query(geo.getEnvelopeInternal());
      for(Object o : ret)
      vb.add(new DBNode(data, (Integer) o));
      return vb;
    }


}
