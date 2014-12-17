/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import generation.v3room.V3Geometry;
import generation.v3room.V3RoomGenerator;
import generation.v3room.V3RoomTemplate;
import java.util.Random;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class P2RoomGenerator extends V3RoomGenerator<P2Container, P2Room, P2KeyType>{

  public P2RoomGenerator(P2ContainerProgress inProgressParent, FinitePrioritizedCollection<P2RoomTemplate> templates, Random random) {
    super(inProgressParent, templates, random);
  }

  @Override
  public P2Room generateRoom() {
    P2Room room = super.generateRoom();
    if(room != null) {
      getTemplates().consume(room.getTemplate());
    }
    return room;
  }

  @Override
  public FinitePrioritizedCollection<V3RoomTemplate<P2Room, P2KeyType>> getTemplates() {
    return (FinitePrioritizedCollection<V3RoomTemplate<P2Room, P2KeyType>>) super.getTemplates();
  }

  @Override
  protected P2Room createRoom(V3RoomTemplate<P2Room, P2KeyType> template, V3Geometry.V3GeometryTransformation<P2Room> xform) {
    return P2Room.create((P2RoomTemplate) template, xform);
  }
}
