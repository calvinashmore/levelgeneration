/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author ashmore
 */
@RunWith(JUnit4.class)
public class Transformation3iTest {

  @Test
  public void testTranslationEquivalence() {
    Transformation3i t1 = Transformation3i.translation(4, 0, 0);
    Transformation3i t2 = Transformation3i.translation(1, 1, 1);
    Transformation3i t3 = Transformation3i.translation(5, 1, 1);

    Assert.assertEquals(t3, t1.compose(t2));
    Assert.assertEquals(t3, t2.compose(t1));
  }

  @Test
  public void testRotationEquivalence() {
    Transformation3i r1 = Transformation3i.rotationZ(1);

    Assert.assertEquals(Transformation3i.IDENTITY, r1.compose(r1).compose(r1).compose(r1));

    Assert.assertEquals(
            Transformation3i.REFLECTION_X.compose(Transformation3i.REFLECTION_Y), r1.compose(r1));
    Assert.assertEquals(
            Transformation3i.REFLECTION_Y.compose(Transformation3i.REFLECTION_X), r1.compose(r1));
  }
}
