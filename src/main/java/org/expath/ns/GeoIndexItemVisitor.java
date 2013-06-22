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

public class GeoIndexItemVisitor implements ItemVisitor{
  /** QName. */
  QNm qname;
  /** Data. */
  Data data;
  /** Nodes. */
  List<DBNode> nodes = new ArrayList<DBNode>();

  /**
   * Default constructor.
   * @param d Data
   */
  public GeoIndexItemVisitor(final Data d) {
    this.data = d;
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
    nodes.add(dn);
  }

}
