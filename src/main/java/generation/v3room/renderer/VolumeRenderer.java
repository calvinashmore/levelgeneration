/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room.renderer;

import com.google.common.collect.ImmutableList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.stream.Collectors;
import math3i.Point3i;
import math3i.Volume3i;

/**
 * Renders a Volume3i at the specified level.
 * Currently renders walls as though they are all internal.
 */
public class VolumeRenderer {

  private final int cellSize;
  private final int borderSize;
  private final int lineWidth;
  private final Color lineColor;

  public VolumeRenderer() {
    this(100, 10, 3, Color.BLACK);
  }

  public VolumeRenderer(int cellSize, int borderSize, int lineWidth, Color lineColor) {
    this.cellSize = cellSize;
    this.borderSize = borderSize;
    this.lineWidth = lineWidth;
    this.lineColor = lineColor;
  }

  private static final List<Point3i> CORNERS = ImmutableList.of(
      Point3i.create(1, 1, 0),
      Point3i.create(-1, 1, 0),
      Point3i.create(-1, -1, 0),
      Point3i.create(1, -1, 0));

  public void render(Volume3i volume, int renderElevation, Graphics2D g) {

    Iterable<Point3i> pointsAtElevation = volume.getPoints().stream()
        .filter(p -> p.getZ() == renderElevation)
        .collect(Collectors.toList());

    for(Point3i point : pointsAtElevation) {
      AffineTransform transform = g.getTransform();
      g.translate(point.getX()*cellSize, point.getY()*cellSize);
      renderCell(g, volume, point);
      g.setTransform(transform);
    }
  }

  /**
   * Render the cell after transformation.
   */
  private void renderCell(Graphics2D g, Volume3i volume, Point3i c) {
    for(Point3i k : CORNERS) {
      boolean kx = volume.contains(c.add(Point3i.create(k.getX(), 0, 0)));
      boolean ky = volume.contains(c.add(Point3i.create(0, k.getY(), 0)));
      boolean kxy = volume.contains(c.add(k));

      int horzY = k.getY() > 0 ? cellSize - borderSize : borderSize;
      int vertX = k.getX() > 0 ? cellSize - borderSize : borderSize;
      int endX = k.getX() > 0 ? cellSize : 0;
      int endY = k.getY() > 0 ? cellSize : 0;
      int mid = cellSize/2;

      g.setColor(lineColor);
      g.setStroke(new BasicStroke(lineWidth));

      if(kx && !ky) {
        // has a neighbor on the x axis. Draw line from middle to that corner.
        g.drawLine(mid, horzY, endX, horzY);
      } else if(ky && !kx) {
        // same thing, but vertical
        g.drawLine(vertX, mid, vertX, endY);
      } else if (kx && ky && !kxy) {
        // this is an internal corner
        g.drawLine(vertX, horzY, endX, horzY);
        g.drawLine(vertX, horzY, vertX, endY);
      } else if (!kx && !ky) {
        // external corner
        g.drawLine(mid, horzY, vertX, horzY);
        g.drawLine(vertX, mid, vertX, horzY);
      }
    }
  }
}
