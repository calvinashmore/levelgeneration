/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation;

import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nullable;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public abstract class RoomGenerator<Parent extends Room<Parent,T,?>, T extends Room<T,?,K>, K extends KeyType> {

  private final InProgressRoom<Parent, T, K> inProgressParent;
  private final PrioritizedCollection<? extends RoomTemplate<T,K>> templates;

  public RoomGenerator(InProgressRoom<Parent, T, K> inProgressParent,
          PrioritizedCollection<? extends RoomTemplate<T,K>> templates) {
    this.inProgressParent = Preconditions.checkNotNull(inProgressParent);
    this.templates = Preconditions.checkNotNull(templates);
  }

  public InProgressRoom<Parent, T,K> getInProgressParent() {
    return inProgressParent;
  }

  public PrioritizedCollection<? extends RoomTemplate<T,K>> getTemplates() {
    return templates;
  }

  public abstract List<? extends Geometry.GeometryTransformation<T>> getPossibleTransformations(
          RoomTemplate<T,K> template,
          List<? extends ConnectionTransformation<T>> highestPriorityConnections);

  /**
   * Returns null if one cannot be generated.
   */
  @Nullable
  public abstract T generateRoom();
}
