/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import com.google.common.collect.Iterables;
import generation.ConnectionPlacement;
import generation.v3room.V3Geometry;
import generation.v3room.V3RoomGenerator;
import generation.v3room.V3RoomTemplate;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class P2RoomGenerator extends V3RoomGenerator<P2Container, P2Room, P2KeyType>{

  public P2RoomGenerator(P2ContainerProgress inProgressParent, FinitePrioritizedCollection<P2RoomTemplate> templates, Random random) {
    super(inProgressParent, templates, random);
  }

  private static final int ATTEMPTS = 3;

  @Override
  public P2Room generateRoom() {

    P2Room room = null;

    for(int attempt=0; attempt<ATTEMPTS && room == null; attempt++) {
      room = super.generateRoom();
    }
    if(room != null) {
      getTemplates().consume(room.getTemplate());
    }
    return room;
  }

  @Override
  protected V3Geometry.V3GeometryTransformation<P2Room> chooseTransform(V3RoomTemplate<P2Room, P2KeyType> roomTemplate,
          List<V3Geometry.V3GeometryTransformation<P2Room>> allowableTransforms, Random random) {

    // ********** Prioritize or weight the transforms based on whether they create new
    // river openings. It's fine to start a river, but we don't want to have lots of river chunks that make invalid maps.

    PrioritizedCollection<V3Geometry.V3GeometryTransformation<P2Room>> prioritizedTransforms = new PrioritizedCollection<>();
    for(V3Geometry.V3GeometryTransformation<P2Room> xform : allowableTransforms) {
      double weight = weightTransform(xform, roomTemplate);
      prioritizedTransforms.addEntry(xform, weight, 1);
    }

    return prioritizedTransforms.choose(random);
  }

  @Override
  public FinitePrioritizedCollection<V3RoomTemplate<P2Room, P2KeyType>> getTemplates() {
    return (FinitePrioritizedCollection<V3RoomTemplate<P2Room, P2KeyType>>) super.getTemplates();
  }

  @Override
  protected P2Room createRoom(V3RoomTemplate<P2Room, P2KeyType> template, V3Geometry.V3GeometryTransformation<P2Room> xform) {
    return P2Room.create((P2RoomTemplate) template, xform);
  }

  private double weightTransform(V3Geometry.V3GeometryTransformation<P2Room> xform, V3RoomTemplate<P2Room, P2KeyType> template) {
    P2ContainerProgress inProgressParent = (P2ContainerProgress) getInProgressParent();
    Collection<ConnectionPlacement<P2Room, P2KeyType>> parentOpenConnections = inProgressParent.getOpenConnections();

    int currentFeatures = (int) parentOpenConnections.stream()
            .map(ConnectionPlacement::getConnection)
            .filter(c -> ((P2ConnectionTemplate) c).isFeature())
            .count();

    int newFeatures = 0;
    int closedFeatures = 0;

    int neighborsHaveOpenFeatures = 0;

    List<ConnectionPlacement<P2Room, P2KeyType>> transformedPlacements = template.getConnections().stream()
            .map(placement -> placement.transform(xform))
            .collect(Collectors.toList());

    for (ConnectionPlacement<P2Room, P2KeyType> placement : transformedPlacements) {
      boolean isFeature = ((P2ConnectionTemplate) placement.getConnection()).isFeature();
      boolean isMatching = inProgressParent.getConnectionAt(placement.getTransform()) != null;
      if(isFeature && !isMatching) newFeatures++;
      if(isFeature && isMatching) closedFeatures++;

      Set<P2Room> childrenAtConnection =
              inProgressParent.getChildrenAtConnection(placement.getTransform().transform(xform));
      if(!childrenAtConnection.isEmpty()) {
        // if this is nonempty, it should contain only one entry.
        // ***********
        // P2Room neighbor = Iterables.getOnlyElement(childrenAtConnection);
        P2Room neighbor = childrenAtConnection.iterator().next();

        // loop through children connections & find unmatched ones with features
        neighborsHaveOpenFeatures += (int) neighbor.getConnectionPlacements().stream()
                // filter only features
                .filter(neighborPlacement -> ((P2ConnectionTemplate)neighborPlacement.getConnection()).isFeature())
                // filter out connections that will match the new transform
                .filter(neighborPlacement -> !transformedPlacements.stream()
                    .anyMatch(neighborPlacement::matches))
                // filter down to remaining open connections
                .filter(neighborPlacement -> parentOpenConnections.stream()
                    .anyMatch(neighborPlacement::matches))
                .count();

      }
    }

    int delta = newFeatures - closedFeatures;

    // if we have several open features, and this introduces more, reject it
    if(currentFeatures > 2 && delta > 0)
      return .0001;

    // if this closes some features, encourage it significantly
    if(delta < 0)
      return 3 * (-delta);

    // this introduces new features, but not an egregious number (maybe)
    if(delta > 0) {
      return Math.pow(2, -delta);
    }

    return 1;
  }
}
