/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import math3i.Point3i;
import phase1.P1ConnectionTransformation;
import phase1.P1Geometry;

/**
 *
 * @author ashmore
 */
public class ConnectionRenderer {

  private final int cellSize;
  private final int borderSize;
  private final int lineWidth;
  private final Color lineColor;

  public ConnectionRenderer() {
    this(100, 10, 3, Color.BLUE);
  }

  public ConnectionRenderer(int cellSize, int borderSize, int lineWidth, Color lineColor) {
    this.cellSize = cellSize;
    this.borderSize = borderSize;
    this.lineWidth = lineWidth;
    this.lineColor = lineColor;
  }

  public void render(P1ConnectionTransformation connection, Graphics2D g) {
    Point3i position = connection.getPosition();
    Point3i facing = connection.getFacing();

    g.setColor(lineColor);
    g.setStroke(new BasicStroke(lineWidth));

    // the offset in here helps us differentiate from which side the connection originated.
    if(facing.getX() != 0) {
      // vertical
      int xPos = position.getX() + (facing.getX() > 0 ? 1 : 0);
      int offset = facing.getX() * borderSize / 2;
      g.drawLine(cellSize*xPos - offset, cellSize*position.getY() + borderSize,
                 cellSize*xPos - offset, cellSize*(position.getY()+1) - borderSize);

    } else if(facing.getY() != 0) {
      // horizontal
      int yPos = position.getY() + (facing.getY() > 0 ? 1 : 0);
      int offset = facing.getY() * borderSize / 2;
      g.drawLine(cellSize*position.getX() + borderSize, cellSize*yPos - offset,
                 cellSize*(position.getX()+1) - borderSize, cellSize*yPos - offset);
    }
  }
}
