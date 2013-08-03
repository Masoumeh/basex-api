package org.expath.ns;

import java.util.*;

import org.basex.data.*;
import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.util.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.*;

/**
 * This Class implements the ItemVisitor interface
 * to apply different methods in a query process of index tree.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Masoumeh Seydi
 */

public class GeoIndexItemVisitor implements ItemVisitor{
  /** QName. */
  QNm qname;
  /** Data. */
  Data data;
  /** Function. */
  String function;
  /** Geometry. */
  Geometry geomtry;
  /** BaseX Gml Reader. */
  GmlReader gmlReader = new GmlReader();
  /** Nodes. */
  List<DBNode> nodes = new ArrayList<DBNode>();
  long visit;
  long test;
  Performance p = new Performance();

  /**
   * Default constructor.
   * @param d Data
   */
  public GeoIndexItemVisitor(final Data d, final String func, final Geometry geo) {
    this.data = d;
    this.function = func;
    this.geomtry = geo;
  }

  /**
   * Property to get the list of nodes.
   * @return nodes
   */
  public List<DBNode> getList() {
    return nodes;
  }

  @Override
  public void visitItem(final Object item) {

    DBNode dn = new DBNode(data, (Integer) item);
//    Geometry tmp = null;
//    try {
//      tmp = gmlReader.createGeometry(dn);
//    } catch(QueryException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    if (geomtry.contains(tmp))
      nodes.add(dn);
  }

}
