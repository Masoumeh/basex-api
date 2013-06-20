package org.expath.ns;

import java.util.*;

import org.basex.data.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;

import com.vividsolutions.jts.index.*;

/**
 * This Class implements the ItemVisitor interface
 * to apply different methods in a query process of index tree.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Masoumeh Seydi
 */

public class GeoItemVisitor implements ItemVisitor{
  /** QName. */
  QNm qname;
  /** Data. */
  Data data;
  /** Nodes. */
  List<DBNode> nodes = new ArrayList<DBNode>();
  //ValueBuilder nodes = new ValueBuilder();
  /**
   * Default constructor.
   * @param d Data
   */
  public GeoItemVisitor(final Data d) {
    this.data = d;
  }

  /**
   * Property to get the list of nodes.
   * @return nodes
   */
  public List<DBNode> /*Value*/ getList() {
    System.out.println("visit size: " + nodes.size());
    //return nodes;
    return nodes;
  }

  @Override
  public void visitItem(final Object item) {
    DBNode dn = new DBNode(data, (Integer) item);
   /* for geo function use only */
 //   ValueBuilder vb = new ValueBuilder();
  //  vb.add(dn);
    nodes.add(dn);
  }

}
