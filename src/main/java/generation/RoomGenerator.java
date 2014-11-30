/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import java.util.List;
import javax.annotation.Nullable;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public abstract class RoomGenerator<Parent extends Room<Parent,T>, T extends Room<T,?>> {

  private final InProgressRoom<Parent, T> inProgressParent;
  private final PrioritizedCollection<? extends RoomTemplate<T>> templates;

  public RoomGenerator(InProgressRoom<Parent, T> inProgressParent,
          PrioritizedCollection<? extends RoomTemplate<T>> templates) {
    this.inProgressParent = inProgressParent;
    this.templates = templates;
  }

  public InProgressRoom<Parent, T> getInProgressParent() {
    return inProgressParent;
  }

  public PrioritizedCollection<? extends RoomTemplate<T>> getTemplates() {
    return templates;
  }

  public abstract List<? extends Geometry.GeometryTransformation<T>> getPossibleTransformations(RoomTemplate<T> template);

  /**
   * Returns null if one cannot be generated.
   */
  @Nullable
  public abstract T generateRoom();
}
