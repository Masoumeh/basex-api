package org.expath.ns;

import org.basex.data.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
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
  ValueBuilder nodes = new ValueBuilder();
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
  public Value getList() {
    return nodes.value();
  }

  @Override
  public void visitItem(final Object item) {
    DBNode dn = new DBNode(data, (Integer) item);
    nodes.add(dn);
  }

}
