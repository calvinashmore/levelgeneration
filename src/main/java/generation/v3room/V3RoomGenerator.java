/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room;

import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.ConnectionTransformation;
import generation.KeyType;
import generation.Room;
import generation.RoomGenerator;
import generation.RoomTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import math3i.Transformation3i;
import math3i.Volume3i;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public abstract class V3RoomGenerator<Parent extends Room<Parent,T,?>, T extends V3Room<T,?,K>, K extends KeyType> extends RoomGenerator<Parent, T, K> {

  private final Random random;

  public V3RoomGenerator(V3ContainerProgress<Parent,T,K> inProgressParent, PrioritizedCollection<? extends V3RoomTemplate<T, K>> templates, Random random) {
    super(inProgressParent, templates);
    this.random = random;
  }

  @Override
  public PrioritizedCollection<V3RoomTemplate<T,K>> getTemplates() {
    return (PrioritizedCollection<V3RoomTemplate<T,K>>) super.getTemplates();
  }

  @Override
  public V3ContainerProgress<Parent,T,K> getInProgressParent() {
    return (V3ContainerProgress<Parent,T,K>) super.getInProgressParent();
  }

  /**
   * Gets the allowable transforms for this template, checking both space and connections.
   */
  @Override
  public List<V3Geometry.V3GeometryTransformation<T>> getPossibleTransformations(
          RoomTemplate<T,K> template,
          List<? extends ConnectionTransformation<T>> highestPriorityConnections) {
    V3RoomTemplate<T,K> template1 = (V3RoomTemplate<T,K>)template;
    Volume3i templateVolume = ((V3Geometry<T>) template.getGeometry()).getVolume();

    Set<Transformation3i> rotations = ImmutableSet.of(
            Transformation3i.rotationZ(0),
            Transformation3i.rotationZ(1),
            Transformation3i.rotationZ(2),
            Transformation3i.rotationZ(3));

    List<Transformation3i> validTransformations = new ArrayList<>();
    for(Transformation3i rotation : rotations) {
      for (Transformation3i translation : getInProgressParent().getFreeVolume()
              .getValidTranslations(templateVolume.transform(rotation))) {
        validTransformations.add(translation.compose(rotation));
      }
    }

    return (List<V3Geometry.V3GeometryTransformation<T>>) validTransformations.stream()
            .map(transform -> V3Geometry.V3GeometryTransformation.create(transform))
            .filter(geom -> getInProgressParent().isValidChildPlacement(template1, geom))
            .filter(geom -> template.getConnections().stream()
                .map(ConnectionPlacement::getTransform)
                .map(transform -> transform.transform((V3Geometry.V3GeometryTransformation<T>) geom))
                .anyMatch(t -> highestPriorityConnections.stream()
                    .anyMatch(t::matches)))
            .collect(Collectors.toList());
  }

  protected abstract T createRoom(V3RoomTemplate<T,K> template, V3Geometry.V3GeometryTransformation<T> xform);

  @Override
  @Nullable
  public T generateRoom() {

    V3ContainerProgress parent = getInProgressParent();
    List<ConnectionPlacement<T, K>> highestPriorityConnectionPlacements =
            parent.getHighestPriorityConnections();
    List<V3ConnectionTransformation<T>> highestPriorityConnections = highestPriorityConnectionPlacements.stream()
            .map(placement -> (V3ConnectionTransformation<T>) placement.getTransform())
            .collect(Collectors.toList());

    if(highestPriorityConnectionPlacements.isEmpty()) {
      return null;
    }

    // **** getPossibleTransform returns weights/??
    Map<V3RoomTemplate<T,K>, List<V3Geometry.V3GeometryTransformation<T>>> templateToTransforms =
        getTemplates().getAllValues().stream()
            .collect(Collectors.toMap(Function.identity(), t -> getPossibleTransformations(t, highestPriorityConnections)));

    V3RoomTemplate chosenTemplate = (V3RoomTemplate) getTemplates().choose(
            t -> !templateToTransforms.get(t).isEmpty(), random);
    if(chosenTemplate == null) {
      return null;
    }

    List<V3Geometry.V3GeometryTransformation<T>> allowableTransforms = templateToTransforms.get(chosenTemplate);
    V3Geometry.V3GeometryTransformation<T> transform = allowableTransforms.get(random.nextInt(allowableTransforms.size()));

    return (T) createRoom(chosenTemplate, transform);
  }
}
