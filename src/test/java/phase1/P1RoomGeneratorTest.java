/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;
import generation.ConnectionPlacement;
import generation.v3room.V3ConnectionTransformation;
import generation.v3room.V3Geometry;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
@RunWith(MockitoJUnitRunner.class)
public class P1RoomGeneratorTest {

  private static final P1RoomTemplate SINGLE_CELL_ROOM = P1RoomTemplate.create(V3Geometry.create(Volume3i.EMPTY).create(Volume3i.box(1, 1, 1)),
          ImmutableSet.of(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, V3ConnectionTransformation.create(Point3i.ZERO, Point3i.UNIT_X))));

  private @Mock Random random;

  private P1RoomGenerator buildRoomGenerator(
          P1ContainerProgress inProgressParent, PrioritizedCollection<P1RoomTemplate> templates) {
    return new P1RoomGenerator(inProgressParent, templates, random);
  }

  @Test
  public void testGetPossibleTransformations() {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(2, 1, 1));

    PrioritizedCollection<P1RoomTemplate> templates = new PrioritizedCollection<>();
    templates.addEntry(SINGLE_CELL_ROOM, 1, 1);

    // add some doors
    container.addParentConnection(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, V3ConnectionTransformation.create(Point3i.ZERO, Point3i.UNIT_Y)));
    container.addParentConnection(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, V3ConnectionTransformation.create(Point3i.UNIT_X, Point3i.UNIT_Y.multiply(-1))));

    P1RoomGenerator roomGenerator = buildRoomGenerator(container, templates);
    List<V3Geometry.V3GeometryTransformation<P1Room>> possibleTransformations = roomGenerator.getPossibleTransformations(SINGLE_CELL_ROOM,
            container.getOpenConnections().stream()
                    .map(placement -> placement.getTransform())
                    .collect(Collectors.toList()));

    List<V3Geometry.V3GeometryTransformation> expectedTransformations = ImmutableList.of(
        V3Geometry.V3GeometryTransformation.create(Transformation3i.rotationZ(3)),
        V3Geometry.V3GeometryTransformation.create(Transformation3i.translation(1, 0, 0).compose(Transformation3i.rotationZ(1)))
        );

    Truth.assertThat(possibleTransformations).containsExactlyElementsIn(expectedTransformations);
  }

  // TODO write some tests that handle highestPriorityConnections

  @Test
  public void testFillRoom() {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(2, 1, 1));

    PrioritizedCollection<P1RoomTemplate> templates = new PrioritizedCollection<>();
    templates.addEntry(SINGLE_CELL_ROOM, 1, 1);

    // add some doors
    container.addParentConnection(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, V3ConnectionTransformation.create(Point3i.ZERO, Point3i.UNIT_Y)));
    container.addParentConnection(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, V3ConnectionTransformation.create(Point3i.UNIT_X, Point3i.UNIT_Y.multiply(-1))));

    P1RoomGenerator roomGenerator = buildRoomGenerator(container, templates);

    // has space for two rooms using the given template.
    container.addChild(roomGenerator.generateRoom());
    container.addChild(roomGenerator.generateRoom());

    Truth.assertThat(roomGenerator.generateRoom()).isNull();
    Truth.assertThat(container.isValid()).isTrue();
  }
}
