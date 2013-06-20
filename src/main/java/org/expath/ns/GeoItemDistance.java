package org.expath.ns;

import org.basex.data.*;
import org.basex.io.out.*;
import org.basex.io.serial.*;
import org.basex.query.value.node.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.strtree.*;
import com.vividsolutions.jts.io.gml2.*;

/**
 * Geo Item Distance class implements the distance function
 * which defines the distance between two objects.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Masoumeh Seydi
 */
public class GeoItemDistance implements ItemDistance {
  /** Data. */
  Data data;

  /**
   * The distance function of two geometry is defined by this method.
   * @param d Data
   */
  public GeoItemDistance(final Data d) {
    data = d;
  }

  @Override
  public double distance(final ItemBoundable item1, final ItemBoundable item2) {
    Geometry geom1 = null, geom2 = null;
    GeometryItemDistance gid = new GeometryItemDistance();
    try {
      GMLReader gmlr = new GMLReader();
      GeometryFactory geometryFactory = new GeometryFactory();
      ArrayOutput ao1 = new ArrayOutput();
      ArrayOutput ao2 = new ArrayOutput();
      Serializer ser1 = Serializer.get(ao1);
      Serializer ser2 = Serializer.get(ao2);
      ser1.serialize(new DBNode(data, (Integer) item1.getItem()));
      ser2.serialize(new DBNode(data, (Integer) item2.getItem()));
      geom1 = gmlr.read(ao1.toString(), geometryFactory);
      geom2 = gmlr.read(ao2.toString(), geometryFactory);
    } catch(Exception e) {
      e.printStackTrace();
    }
    return gid.distance(new ItemBoundable(geom1.getEnvelopeInternal(), geom1),
        new ItemBoundable(geom2.getEnvelopeInternal(), geom2));
   }
}
