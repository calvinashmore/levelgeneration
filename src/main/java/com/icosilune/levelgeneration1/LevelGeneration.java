/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icosilune.levelgeneration1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import math.Transformation3i;
import math.Volume3i;

/**
 *
 * @author ashmore
 */
public class LevelGeneration {

  private static final Random random = new Random();

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here

    int s = 10;
    Volume3i v = Volume3i.box(s, s, s);
    System.out.println("Starting...");

    Map<Transformation3i, Volume3i> packed = packVolumeWithBoxes(v, s);

    for(Map.Entry<Transformation3i, Volume3i> entry : packed.entrySet()) {

      Volume3i box = entry.getValue().transform(entry.getKey());

      System.out.println(box.getDimensions()+": at "+box.getMinimum());
    }

  }

  /**
   * Take a volume and attempt to fill it with boxes.
   * FIRST THING TO DO: Find a better way of doing this. If we don't assume things are already box shaped, the packing algorithm gets weird.
   * Research packing algorithms?
   */
  public static Map<Transformation3i, Volume3i> packVolumeWithBoxes(Volume3i volume, int maxSize) {

    Volume3i union = Volume3i.EMPTY;

    Map<Transformation3i, Volume3i> currentBoxes = new HashMap<>();
    int attempts = 0;

    while(!union.contains(volume)) {
      attempts++;

      if (attempts > 10000) {
        System.out.println("OMFG");
        return currentBoxes;
      }

      // make a box
      Volume3i box = Volume3i.box(
              random.nextInt(maxSize/2)+1,
              random.nextInt(maxSize/2)+1,
              random.nextInt(maxSize/2)+1);

      // try to put it somewhere
      Transformation3i t = findTransformation(box, union, volume, maxSize);
      if (t == null)
        continue;

      currentBoxes.put(t, box);
      union = union.union(box);
    }
    return currentBoxes;
  }

  // returns null if cannot be found after some searching
  private static Transformation3i findTransformation(Volume3i box, Volume3i toNotIntersect, Volume3i container, int maxSize) {
    for(int i=0;i<100;i++){
      Transformation3i t = Transformation3i.translation(random.nextInt(maxSize), random.nextInt(maxSize), random.nextInt(maxSize));
      Volume3i transformed = box.transform(t);
      if(!transformed.intersects(toNotIntersect) && container.contains(transformed))
        return t;
    }
    return null;
  }

}