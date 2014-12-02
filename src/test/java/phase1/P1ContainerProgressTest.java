/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.common.collect.ImmutableSet;
import generation.ConnectionTemplate;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ashmore
 */
@RunWith(MockitoJUnitRunner.class)
public class P1ContainerProgressTest {


  private static final ConnectionTemplate.ConnectionPlacement<P1Room> CONNECTION_1 =
          ConnectionTemplate.ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, P1Geometry.P1ConnectionTransformation.create(Point3i.ZERO, Point3i.UNIT_X));
  private static final ConnectionTemplate.ConnectionPlacement<P1Room> CONNECTION_2 =
          ConnectionTemplate.ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, P1Geometry.P1ConnectionTransformation.create(Point3i.create(10, 0, 0), Point3i.UNIT_X));
  private static final ConnectionTemplate.ConnectionPlacement<P1Room> CONNECTION_3 =
          ConnectionTemplate.ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, P1Geometry.P1ConnectionTransformation.create(Point3i.ZERO, Point3i.UNIT_X.multiply(-1)));
  private static final P1RoomTemplate SINGLE_CELL_ROOM = P1RoomTemplate.create(P1Geometry.create(Volume3i.box(1, 1, 1)),
          ImmutableSet.of(CONNECTION_1));

  @Test
  public void testGetConnectionAt() {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(1, 1, 1));

    container.addConnection(CONNECTION_1);
    Assert.assertEquals(CONNECTION_1, container.getConnectionAt(CONNECTION_1.getTransform()));
    Assert.assertEquals(CONNECTION_1, container.getConnectionAt(CONNECTION_1.getTransform().getOpposite()));
  }

  @Test
  public void testConnectionsMatch() {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(1, 1, 1));
    P1Geometry.P1GeometryTransformation identityTransform = P1Geometry.P1GeometryTransformation.create(Transformation3i.IDENTITY);

    // parent has no connections
    Assert.assertFalse(container.connectionsMatch(SINGLE_CELL_ROOM, identityTransform));

    // parent has a connection that matches this exactly
    container.addConnection(CONNECTION_1);
    Assert.assertTrue(container.connectionsMatch(SINGLE_CELL_ROOM, identityTransform));

    // parent has a connection that is far away that we don't care about
    container.addConnection(CONNECTION_2);
    Assert.assertTrue(container.connectionsMatch(SINGLE_CELL_ROOM, identityTransform));

    // parent has a connection opposite ours
    container.addConnection(CONNECTION_3);
    Assert.assertFalse(container.connectionsMatch(SINGLE_CELL_ROOM, identityTransform));
  }
}
