/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.KeyType;
import generation.RoomTemplate;
import generation.RoomTemplateGenerator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author ashmore
 */
public abstract class V3RoomTemplateGenerator<T extends V3Room<T,?,K>, K extends KeyType> extends RoomTemplateGenerator<T, K>{

  public V3RoomTemplateGenerator(V3Geometry<T> geometry) {
    super(geometry);
  }

  @Override
  protected boolean isValid(RoomTemplate<T, K> template) {
    V3RoomTemplate<T,K> template1 = (V3RoomTemplate<T,K>) template;
    // needs at least one connection that's not a wall
    boolean hasDoor = template.getConnections().stream()
            .map(ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .anyMatch(v -> v > 0);

    boolean hasAdjacentDoors = template.getConnections().stream()
            .filter(placement -> placement.getConnection().getMatchPriority() > 0)
            .map(ConnectionPlacement::getTransform)
            .anyMatch(xform -> {
              V3ConnectionTransformation xform1 = (V3ConnectionTransformation) xform;
              return V3Geometry.DIRECTIONS.stream()
                      .map(direction -> template1.getConnectionAt(xform1.getPosition().add(direction), xform1.getFacing()))
                      .filter(Objects::nonNull)
                      .map(ConnectionTemplate::getMatchPriority)
                      .anyMatch(priority -> priority > 0);
            } );

    return hasDoor && !hasAdjacentDoors;
  }

//  @Override
//  protected RoomTemplate<T, K> createTemplate(Set<ConnectionPlacement<P1Room, P1KeyType>> placements) {
//    return P1RoomTemplate.create((V3Geometry<P1Room>) getGeometry(), placements);
//  }

  @Override
  public List<? extends V3RoomTemplate<T,K>> generateTemplates() {
    return (List<V3RoomTemplate<T,K>>) super.generateTemplates();
  }

//  @Override
//  public V3RoomTemplateGenerator addConnections(ConnectionTransformation<P1Room> transform, ConnectionTemplate<P1Room, P1KeyType>... connections) {
//    return (V3RoomTemplateGenerator) super.addConnections(transform, connections);
//  }
//
//  @Override
//  public V3RoomTemplateGenerator addConnections(ConnectionTransformation<P1Room> transform, Iterable<ConnectionTemplate<P1Room, P1KeyType>> connections) {
//    return (V3RoomTemplateGenerator) super.addConnections(transform, connections);
//  }
}
