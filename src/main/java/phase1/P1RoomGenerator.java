/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import generation.v3room.V3Geometry;
import generation.v3room.V3RoomGenerator;
import generation.v3room.V3RoomTemplate;
import java.util.Random;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class P1RoomGenerator extends V3RoomGenerator<P1Container, P1Room, P1KeyType> {

  public P1RoomGenerator(P1ContainerProgress inProgressParent, PrioritizedCollection<P1RoomTemplate> templates, Random random) {
    super(inProgressParent, templates, random);
  }

  @Override
  protected P1Room createRoom(V3RoomTemplate<P1Room, P1KeyType> template, V3Geometry.V3GeometryTransformation<P1Room> transform) {
    return P1Room.create((P1RoomTemplate) template, transform);
  }
}
