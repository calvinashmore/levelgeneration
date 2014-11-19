/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math3i;

import math3i.Volume3i;
import math3i.Transformation3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;
import java.util.Collection;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author ashmore
 */
@RunWith(Parameterized.class)
public class Volume3iGetValidTranslationTest {

  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return ImmutableList.<Object[]>builder()
            .add(new Object[] {
              // both boxes are unit
              Volume3i.box(1, 1, 1),
              Volume3i.box(1, 1, 1),
              ImmutableSet.of(Transformation3i.IDENTITY)
            })
            .add(new Object[] {
              // both boxes are unit, but containing box is translated
              Volume3i.box(1, 1, 1).transform(Transformation3i.translation(5, 0, 0)),
              Volume3i.box(1, 1, 1),
              ImmutableSet.of(Transformation3i.translation(5, 0, 0))
            })
            .add(new Object[] {
              // both boxes are unit, but contained box is translated
              Volume3i.box(1, 1, 1),
              Volume3i.box(1, 1, 1).transform(Transformation3i.translation(5, 0, 0)),
              ImmutableSet.of(Transformation3i.translation(-5, 0, 0))
            })
            .add(new Object[] {
              // contained box is too big
              Volume3i.box(1, 1, 1),
              Volume3i.box(1, 2, 1),
              ImmutableSet.of()
            })
            .add(new Object[] {
              // box is small and has two entries
              Volume3i.box(1, 2, 1),
              Volume3i.box(1, 1, 1),
              ImmutableSet.of(Transformation3i.IDENTITY, Transformation3i.translation(0,1,0))
            })
            .build();
  }

  private final Volume3i containingVolume;
  private final Volume3i containedVolume;
  private final ImmutableSet<Transformation3i> expectedTranslations;

  public Volume3iGetValidTranslationTest(
          Volume3i containingVolume, Volume3i containedVolume,
          ImmutableSet<Transformation3i> expectedTranslations) {
    this.containingVolume = containingVolume;
    this.containedVolume = containedVolume;
    this.expectedTranslations = expectedTranslations;
  }

  @Test
  public void test() {
    Set<Transformation3i> actualTranslations =
            containingVolume.getValidTranslations(containedVolume);

    Truth.assertThat(expectedTranslations).containsExactlyElementsIn(actualTranslations);
  }
}
