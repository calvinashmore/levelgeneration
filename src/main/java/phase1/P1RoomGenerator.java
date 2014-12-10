/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.ConnectionTemplate;
import generation.ConnectionTransformation;
import generation.RoomGenerator;
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
public class P1RoomGenerator extends RoomGenerator<P1Container, P1Room> {

  private final Random random;

  public P1RoomGenerator(P1ContainerProgress inProgressParent, PrioritizedCollection<P1RoomTemplate> templates, Random random) {
    super(inProgressParent, templates);
    this.random = random;
  }

  @Override
  public PrioritizedCollection<P1RoomTemplate> getTemplates() {
    return (PrioritizedCollection<P1RoomTemplate>) super.getTemplates();
  }

  @Override
  public P1ContainerProgress getInProgressParent() {
    return (P1ContainerProgress) super.getInProgressParent();
  }

  /**
   * Gets the allowable transforms for this template, checking both space and connections.
   */
  @Override
  public List<P1Geometry.P1GeometryTransformation> getPossibleTransformations(
          P1RoomTemplate template,
          List<? extends ConnectionTransformation<P1Room>> highestPriorityConnections) {
    P1RoomTemplate template1 = (P1RoomTemplate) template;
    Volume3i templateVolume = template1.getGeometry().getVolume();

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

    return validTransformations.stream()
            .map(transform -> P1Geometry.P1GeometryTransformation.create(transform))
            .filter(geom -> getInProgressParent().isValidChildPlacement(template, geom))
            .filter(geom -> template.getConnections().stream()
                .map(ConnectionPlacement::getTransform)
                .map(transform -> transform.transform(geom))
                .anyMatch(t -> highestPriorityConnections.stream()
                    .anyMatch(t::matches)))
            .collect(Collectors.toList());
  }

  @Override
  @Nullable
  public P1Room generateRoom() {

    P1ContainerProgress parent = getInProgressParent();
    List<ConnectionPlacement<P1Room, P1KeyType>> highestPriorityConnectionPlacements =
            parent.getHighestPriorityConnections();
    List<P1ConnectionTransformation> highestPriorityConnections = highestPriorityConnectionPlacements.stream()
            .map(placement -> (P1ConnectionTransformation) placement.getTransform())
            .collect(Collectors.toList());

    if(highestPriorityConnectionPlacements.isEmpty()) {
      return null;
    }

    Map<P1RoomTemplate, List<P1Geometry.P1GeometryTransformation>> templateToTransforms =
        getTemplates().getAllValues().stream()
            .collect(Collectors.toMap(Function.identity(), t -> getPossibleTransformations(t, highestPriorityConnections)));

    P1RoomTemplate chosenTemplate = getTemplates().choose(
            t -> !templateToTransforms.get(t).isEmpty(), random);
    if(chosenTemplate == null) {
      return null;
    }

    List<P1Geometry.P1GeometryTransformation> allowableTransforms = templateToTransforms.get(chosenTemplate);
    P1Geometry.P1GeometryTransformation transform = allowableTransforms.get(random.nextInt(allowableTransforms.size()));

    return P1Room.create(chosenTemplate, transform);
  }
}
