/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;
import generation.ConnectionPlacement;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import util.Graph;

/**
 *
 * @author ashmore
 */
@RunWith(MockitoJUnitRunner.class)
public class P1ContainerProgressTest {


  private static final ConnectionPlacement<P1Room, P1KeyType> CONNECTION_1 =
      P1ConnectionTemplate.placement(P1ConnectionTemplate.DOOR_1, Point3i.ZERO, P1Geometry.EAST);
  private static final ConnectionPlacement<P1Room, P1KeyType> CONNECTION_2 =
      P1ConnectionTemplate.placement(P1ConnectionTemplate.DOOR_1, Point3i.create(10, 0, 0), P1Geometry.EAST);
  private static final ConnectionPlacement<P1Room, P1KeyType> CONNECTION_3 =
      P1ConnectionTemplate.placement(P1ConnectionTemplate.DOOR_1, Point3i.ZERO, P1Geometry.WEST);
  private static final P1RoomTemplate SINGLE_CELL_ROOM_1 = P1RoomTemplate.create(P1Geometry.create(Volume3i.box(1, 1, 1)),
          ImmutableSet.of(CONNECTION_1));
  private static final P1RoomTemplate SINGLE_CELL_ROOM_2 = P1RoomTemplate.create(P1Geometry.create(Volume3i.box(1, 1, 1)),
          ImmutableSet.of(CONNECTION_1, CONNECTION_3));

  @Test
  public void testGetConnectionAt() {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(1, 1, 1));

    container.addParentConnection(CONNECTION_1);
    Assert.assertEquals(CONNECTION_1, container.getConnectionAt(CONNECTION_1.getTransform()));
    Assert.assertEquals(CONNECTION_1, container.getConnectionAt(CONNECTION_1.getTransform().getOpposite()));
  }

  @Test
  public void testConnectionsMatch() {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(1, 1, 1));
    P1Geometry.P1GeometryTransformation identityTransform = P1Geometry.P1GeometryTransformation.create(Transformation3i.IDENTITY);

    // parent has no connections
    Assert.assertFalse(container.connectionsMatch(SINGLE_CELL_ROOM_1, identityTransform));

    // parent has a connection that matches this exactly
    container.addParentConnection(CONNECTION_1);
    Assert.assertTrue(container.connectionsMatch(SINGLE_CELL_ROOM_1, identityTransform));

    // parent has a connection that is far away that we don't care about
    container.addParentConnection(CONNECTION_2);
    Assert.assertTrue(container.connectionsMatch(SINGLE_CELL_ROOM_1, identityTransform));

    // parent has a connection opposite ours
    // Requires a room with connections on both ends.
    container.addParentConnection(CONNECTION_3);
    Assert.assertFalse(container.connectionsMatch(SINGLE_CELL_ROOM_1, identityTransform));
  }

  @Test
  public void testBuildConnectionGraph() {

    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(2, 1, 1));
    P1Geometry.P1GeometryTransformation transform2 = P1Geometry.P1GeometryTransformation.create(Transformation3i.IDENTITY);
    P1Geometry.P1GeometryTransformation transform1 = P1Geometry.P1GeometryTransformation.create(Transformation3i.translation(1, 0, 0).compose(Transformation3i.rotationZ(2)));

    container.addParentConnection(CONNECTION_3);

    Assert.assertTrue(container.connectionsMatch(SINGLE_CELL_ROOM_2, transform2));
    Assert.assertTrue(container.connectionsMatch(SINGLE_CELL_ROOM_1, transform1));
    container.addChild(P1Room.create(SINGLE_CELL_ROOM_1, transform1));
    container.addChild(P1Room.create(SINGLE_CELL_ROOM_2, transform2));

    Graph<ConnectionPlacement<P1Room, P1KeyType>, P1KeyType> connectionGraph = container.buildConnectionGraph();
    Truth.assertThat(connectionGraph.getAllNodes()).containsExactly(CONNECTION_3, CONNECTION_3.getOpposite(), CONNECTION_1, CONNECTION_1.getOpposite());
  }
}
