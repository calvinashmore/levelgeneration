/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A collection of T that accounts for weights and priorities of entries.
 * When selecting from a PrioritizedCollection, we return the
 */
public class PrioritizedCollection<T> {

  private final List<Entry<T>> entries = new ArrayList<>();

  public void addEntry(T value, double weight, int priority) {
    entries.add(Entry.create(value, weight, priority));
  }

  public T choose(Predicate<T> predicate, Random random) {
    Iterable<Entry<T>> filteredEntries = Iterables.filter(entries, entryPredicate(predicate));
    int maximumPriority = Ordering.natural().max(Iterables.transform(filteredEntries, priorityFunction()));
    filteredEntries = Iterables.filter(filteredEntries,
            Predicates.compose(Predicates.equalTo(maximumPriority), priorityFunction()));

    double sum = 0;
    for(Entry<T> entry : filteredEntries)
      sum += entry.getWeight();

    double selection = random.nextDouble() * sum;
    for(Entry<T> entry : filteredEntries) {
      selection -= entry.getWeight();
      if(selection <= 0)
        return entry.getValue();
    }
    // should not get here.
    throw new IllegalStateException();
  }

  public ImmutableList<Entry<T>> getEntries() {
    return ImmutableList.copyOf(entries);
  }

  private static <T> Predicate<Entry<T>> entryPredicate(final Predicate<T> basePredicate) {
    return new Predicate<Entry<T>>() {
      @Override public boolean apply(Entry<T> t) {
        return basePredicate.apply(t.getValue());
      }
    };
  }

  private Function<Entry<T>, Integer> priorityFunction() {
    return new Function<Entry<T>, Integer>() {
      @Override public Integer apply(Entry<T> f) {
        return f.getPriority();
      }
    };
  }

  @AutoValue
  public abstract static class Entry<T> {
    public abstract T getValue();
    public abstract double getWeight();
    public abstract int getPriority();

    public static <T> Entry<T> create(T value, double weight, int priority) {
      Preconditions.checkArgument(weight > 0, "Can not have weight less than or equal to zero");
      Preconditions.checkArgument(priority >= 0, "Can not have priority less than zero");
      return new AutoValue_PrioritizedCollection_Entry(value, weight, priority);
    }
  }
}
