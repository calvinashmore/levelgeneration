/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.common.base.Predicates;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ashmore
 */
@RunWith(MockitoJUnitRunner.class)
public class PrioritizedCollectionTest {
  @Mock Random mockRandom;

  @Test
  public void testPriorities() {
    Object obj1 = new Object();
    Object obj2 = new Object();

    PrioritizedCollection<Object> collection = new PrioritizedCollection<>();
    collection.addEntry(obj1, 1.0, 1);
    collection.addEntry(obj2, 1.0, 2);

    Mockito.when(mockRandom.nextDouble()).thenReturn(0.0);
    Assert.assertEquals(obj2, collection.choose(mockRandom));
  }

  @Test
  public void testWithPredicate() {
    Object obj1 = new Object();
    Object obj2 = new Object();

    PrioritizedCollection<Object> collection = new PrioritizedCollection<>();
    collection.addEntry(obj1, 1.0, 1);
    collection.addEntry(obj2, 1.0, 2);

    // obj2 has a higher priority, but it is ruled out by the predicate
    Mockito.when(mockRandom.nextDouble()).thenReturn(0.0);
    Assert.assertEquals(obj1, collection.choose(Predicates.equalTo(obj1), mockRandom));
  }
}
