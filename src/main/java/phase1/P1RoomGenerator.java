/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import generation.ConnectionTemplate.ConnectionPlacement;
import generation.Geometry;
import generation.InProgressRoom;
import generation.RoomGenerator;
import generation.RoomTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class P1RoomGenerator extends RoomGenerator<P1Container, P1Room> {

  private final Random random = new Random();

  public P1RoomGenerator(InProgressRoom<P1Container, P1Room> inProgressParent, PrioritizedCollection<P1RoomTemplate> templates) {
    super(inProgressParent, templates);
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
          RoomTemplate<P1Room> template) {
    P1RoomTemplate template1 = (P1RoomTemplate) template;
    Volume3i templateVolume = template1.getGeometry().getVolume();

    Set<Transformation3i> rotations = ImmutableSet.of(
            Transformation3i.rotationZ(0),
            Transformation3i.rotationZ(1),
            Transformation3i.rotationZ(2),
            Transformation3i.rotationZ(3));

    List<Transformation3i> validTransformations = new ArrayList<>();
    for(Transformation3i rotation : rotations) {
      for (Transformation3i translation
              : getInProgressParent().getFreeVolume()
              .getValidTranslations(templateVolume.transform(rotation))) {
        validTransformations.add(rotation.compose(translation));
      }
    }

    return validTransformations.stream()
            .map(transform -> P1Geometry.P1GeometryTransformation.create(transform))
            .filter(geom -> getInProgressParent().isValidChildPlacement(template, geom))
            .collect(Collectors.toList());
  }

  @Override
  public P1Room generateRoom() {

    Map<P1RoomTemplate, List<P1Geometry.P1GeometryTransformation>> templateToTransforms =
            getTemplates().getAllValues().stream().collect(
                    Collectors.toMap(Function.identity(), t -> getPossibleTransformations(t)));

    P1RoomTemplate chosenTemplate = getTemplates().choose(t -> !templateToTransforms.get(t).isEmpty(), random);
    List<P1Geometry.P1GeometryTransformation> allowableTransforms = templateToTransforms.get(chosenTemplate);
    P1Geometry.P1GeometryTransformation transform = allowableTransforms.get(random.nextInt(allowableTransforms.size()));

    return P1Room.create(chosenTemplate, transform);
  }
}
