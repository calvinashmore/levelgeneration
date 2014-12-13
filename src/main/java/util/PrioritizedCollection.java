/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * A collection of T that accounts for weights and priorities of entries.
 * When selecting from a PrioritizedCollection, we return the
 */
public class PrioritizedCollection<T> {

  // maybe make this a Map<T+priority, Double> ?
  private final List<Entry<T>> entries = new ArrayList<>();

  /**
   * Add an entry to the prioritized collection.
   * If the entry already exists (based on value and priority),
   * remove the existing entry and use the maximum of the weight of that entry and the weight supplied.
   */
  public void addEntry(T value, double weight, int priority) {
    Preconditions.checkNotNull(value);

    List<Entry<T>> toRemove = entries.stream()
            .filter(entry -> entry.getValue().equals(value) && entry.getPriority() == priority)
            .collect(Collectors.toList());

    entries.removeAll(toRemove);
    // toRemove should have only one element.
    // using summingDouble is a bit redundant here if that is the case.
    weight = Math.max(weight, toRemove.stream().collect(Collectors.summingDouble(Entry::getWeight)));
    entries.add(Entry.create(value, weight, priority));
  }

  /**
   * Add the following entries so that the sum of all possibilities has the same weight.
   */
  public void addEntries(Collection<T> values, double weight, int priority) {
    double itemWeight = weight / Iterables.size(values);
    values.forEach(value -> {addEntry(value, itemWeight, priority);});
  }

  /**
   * Add the following entries using the given weighting function.
   */
  public void addEntries(Collection<T> values, Function<T, Double> weightFunction, double totalWeight, int priority) {

    double summedWeight = values.stream()
            .collect(Collectors.summingDouble(weightFunction::apply));

    double scale = totalWeight / summedWeight;
    values.forEach(value -> {addEntry(value, scale*weightFunction.apply(value), priority);});
  }

  public List<T> getAllValues() {
    return entries.stream().map(Entry::getValue).collect(Collectors.toList());
  }

  /**
   * Returns null iff the predicate is false for all entries.
   */
  @Nullable
  public T choose(Predicate<T> predicate, Random random) {
    List<Entry<T>> filteredEntries = entries.stream()
            .filter(t -> predicate.apply(t.getValue()))
            .collect(Collectors.toList());
    if (Iterables.isEmpty(filteredEntries)) {
      return null;
    }

    int maximumPriority = Ordering.natural().max(Iterables.transform(filteredEntries, Entry::getPriority));
    filteredEntries = filteredEntries.stream()
            .filter(t -> t.getPriority() == maximumPriority)
            .collect(Collectors.toList());

    if(Iterables.isEmpty(filteredEntries)) {
      return null;
    }

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

  @Override
  public String toString() {
    return entries.toString();
  }
}
