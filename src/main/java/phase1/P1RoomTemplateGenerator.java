/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.common.base.Predicates;
import generation.ConnectionTemplate;
import generation.Geometry;
import generation.RoomTemplate;
import generation.RoomTemplateGenerator;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author ashmore
 */
public class P1RoomTemplateGenerator extends RoomTemplateGenerator<P1Room>{

  public P1RoomTemplateGenerator(Geometry<P1Room> geometry) {
    super(geometry);
  }

  @Override
  protected boolean isValid(RoomTemplate<P1Room> template) {
    P1RoomTemplate template1 = (P1RoomTemplate) template;
    // needs at least one connection that's not a wall
    boolean hasDoor = template.getConnections().stream()
            .map(ConnectionTemplate.ConnectionPlacement::getConnection)
            .map(ConnectionTemplate::getMatchPriority)
            .anyMatch(v -> v > 0);

    boolean hasAdjacentDoors = template.getConnections().stream()
            .filter(placement -> placement.getConnection().getMatchPriority() > 0)
            .map(ConnectionTemplate.ConnectionPlacement::getTransform)
            .anyMatch( xform -> {
              P1Geometry.P1ConnectionTransformation xform1 = (P1Geometry.P1ConnectionTransformation) xform;
              return P1Geometry.DIRECTIONS.stream()
                      .map(direction -> template1.getConnectionAt(xform1.getPosition().add(direction), xform1.getFacing()))
                      .filter(Objects::nonNull)
                      .map(ConnectionTemplate::getMatchPriority)
                      .anyMatch(priority -> priority > 0);
            } );

    return hasDoor && !hasAdjacentDoors;
  }

  @Override
  protected RoomTemplate<P1Room> createTemplate(Set<ConnectionTemplate.ConnectionPlacement<P1Room>> placements) {
    return P1RoomTemplate.create((P1Geometry) getGeometry(), placements);
  }

  @Override
  public Iterable<P1RoomTemplate> generateTemplates() {
    return (Iterable<P1RoomTemplate>) super.generateTemplates();
  }

  @Override
  public P1RoomTemplateGenerator addConnections(Geometry.ConnectionTransformation<P1Room> transform, ConnectionTemplate<P1Room>... connections) {
    return (P1RoomTemplateGenerator) super.addConnections(transform, connections);
  }

  @Override
  public P1RoomTemplateGenerator addConnections(Geometry.ConnectionTransformation<P1Room> transform, Iterable<ConnectionTemplate<P1Room>> connections) {
    return (P1RoomTemplateGenerator) super.addConnections(transform, connections);
  }
}
