/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.RoomTemplateGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;
import generation.ConnectionPlacement;
import generation.v3room.V3ConnectionTransformation;
import generation.v3room.V3Geometry;
import math3i.Point3i;
import math3i.Volume3i;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author ashmore
 */
@RunWith(JUnit4.class)
public class RoomTemplateGeneratorTest {

  @Test
  public void testGenerateTemplates() {
    V3Geometry geometry = V3Geometry.create(Volume3i.box(1, 1, 1));
    RoomTemplateGenerator generator = new P1RoomTemplateGenerator(geometry);
    V3ConnectionTransformation c1 = V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.WEST);
    V3ConnectionTransformation c2 = V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.EAST);
    generator.addConnections(c1,
            P1ConnectionTemplate.WALL,
            P1ConnectionTemplate.DOOR_1);
    generator.addConnections(c2,
            P1ConnectionTemplate.WALL,
            P1ConnectionTemplate.DOOR_1);

    Truth.assertThat(generator.generateTemplates()).containsExactlyElementsIn(ImmutableList.of(P1RoomTemplate.create(geometry, ImmutableSet.of(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, c1),
            ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, c2))),
        P1RoomTemplate.create(geometry, ImmutableSet.of(ConnectionPlacement.create(P1ConnectionTemplate.WALL, c1),
            ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, c2))),
        P1RoomTemplate.create(geometry, ImmutableSet.of(ConnectionPlacement.create(P1ConnectionTemplate.DOOR_1, c1),
            ConnectionPlacement.create(P1ConnectionTemplate.WALL, c2)))));
  }
}
