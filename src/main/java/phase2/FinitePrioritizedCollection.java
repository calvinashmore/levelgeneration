/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Random;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class FinitePrioritizedCollection<T> extends PrioritizedCollection<T> {

  private final Multiset<T> entryCounts = HashMultiset.create();

  @Override
  public void addEntry(T value, double weight, int priority) {
    super.addEntry(value, weight, priority);
    entryCounts.add(value);
  }

  public void addEntries(T value, double weight, int priority, int count) {
    super.addEntry(value, weight, priority);
    entryCounts.add(value, count);
  }

  void consume(T value) {
    entryCounts.remove(value);
  }

  @Override
  public T choose(Predicate<T> predicate, Random random) {
    return super.choose(Predicates.and(predicate, entryCounts::contains), random);
  }
}
