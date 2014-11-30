/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import generation.ConnectionTemplate;
import generation.RoomTemplate;
import java.util.Set;

/**
 *
 * @author ashmore
 */
@AutoValue
public abstract class P1RoomTemplate implements RoomTemplate<P1Room> {

  @Override
  public abstract P1Geometry getGeometry();

  @Override
  public abstract Set<ConnectionTemplate.ConnectionPlacement<P1Room>> getConnections();

  public static P1RoomTemplate create(P1Geometry geometry, Set<ConnectionTemplate.ConnectionPlacement<P1Room>> connections) {
    return new AutoValue_P1RoomTemplate(geometry, connections);
  }
}
