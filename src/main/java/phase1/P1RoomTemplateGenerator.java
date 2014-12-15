/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.ConnectionTransformation;
import generation.RoomTemplate;
import generation.v3room.V3Geometry;
import generation.v3room.V3RoomTemplateGenerator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ashmore
 */
public class P1RoomTemplateGenerator extends V3RoomTemplateGenerator<P1Room, P1KeyType>{

  public P1RoomTemplateGenerator(V3Geometry<P1Room> geometry) {
    super(geometry);
  }

  @Override
  protected RoomTemplate<P1Room, P1KeyType> createTemplate(Set<ConnectionPlacement<P1Room, P1KeyType>> placements) {
    return P1RoomTemplate.create((V3Geometry<P1Room>) getGeometry(), placements);
  }

  @Override
  public List<P1RoomTemplate> generateTemplates() {
    return (List<P1RoomTemplate>) super.generateTemplates();
  }

  @Override
  public P1RoomTemplateGenerator addConnections(ConnectionTransformation<P1Room> transform, ConnectionTemplate<P1Room, P1KeyType>... connections) {
    return (P1RoomTemplateGenerator) super.addConnections(transform, connections);
  }

  @Override
  public P1RoomTemplateGenerator addConnections(ConnectionTransformation<P1Room> transform, Iterable<ConnectionTemplate<P1Room, P1KeyType>> connections) {
    return (P1RoomTemplateGenerator) super.addConnections(transform, connections);
  }
}
