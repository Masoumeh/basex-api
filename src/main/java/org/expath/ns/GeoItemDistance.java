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
   * @param data Data
   */
  public GeoItemDistance(final Data data) {
    this.data = data;
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
    //geom1.distance(geom2);

//    Nodes n = new Nodes((Integer) item1.getItem(), data);
//    ArrayOutput ao = new ArrayOutput().max(10000);
//    if(n != null)
//      try {
//      n.serialize(Serializer.get(ao));
//    } catch(IOException e1) {
//      // TODO Auto-generated catch block
//      e1.printStackTrace();
//    }
//    GMLReader gmlr = new GMLReader();
//    GeometryFactory geometryFactory = new GeometryFactory();
//    try {
//      geom1 = gmlr.read(ao.toString(), geometryFactory);
//    } catch(SAXException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch(IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch(ParserConfigurationException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//
//    n = new Nodes((Integer) item2.getItem(), data);
//    ao = new ArrayOutput().max(10000);
//    if(n != null) try {
//      n.serialize(Serializer.get(ao));
//    } catch(IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    gmlr = new GMLReader();
//    geometryFactory = new GeometryFactory();
//    try {
//      geom2 = gmlr.read(ao.toString(), geometryFactory);
//    } catch(SAXException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch(IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } catch(ParserConfigurationException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
   }
}
