/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase1.renderer;

import generation.ConnectionPlacement;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import phase1.P1ConnectionTransformation;
import phase1.P1KeyType;
import phase1.P1Room;
import util.Graph;

/**
 *
 * @author ashmore
 */
public class GraphRenderer {
  private final int cellSize;
  private final int borderSize;
  private final int lineWidth;
  private final Color lineColor;

  public GraphRenderer() {
    this(100, 10, 1, Color.GRAY);
  }

  public GraphRenderer(int cellSize, int borderSize, int lineWidth, Color lineColor) {
    this.cellSize = cellSize;
    this.borderSize = borderSize;
    this.lineWidth = lineWidth;
    this.lineColor = lineColor;
  }

  public void render(Graph<ConnectionPlacement<P1Room, P1KeyType>, P1KeyType> graph, Graphics2D g) {

    for (ConnectionPlacement<P1Room, P1KeyType> placement1 : graph.getAllNodes()) {
      for (ConnectionPlacement<P1Room, P1KeyType> placement2 : graph.getConnections(placement1).keySet()) {
        P1ConnectionTransformation transform1 = (P1ConnectionTransformation) placement1.getTransform();
        P1ConnectionTransformation transform2 = (P1ConnectionTransformation) placement2.getTransform();

        g.setStroke(new BasicStroke(lineWidth));
        g.setColor(lineColor);

        final int t1x = getXPos(transform1);
        final int t1y = getYPos(transform1);
        final int t2x = getXPos(transform2);
        final int t2y = getYPos(transform2);

        g.drawOval(t1x-5, t1y-5, 10, 10);
        g.drawOval(t2x-5, t2y-5, 10, 10);

        g.drawLine(t1x, t1y, t2x, t2y);
      }
    }
  }

  private int getXPos(P1ConnectionTransformation xform) {
    return xform.getPosition().getX()*cellSize
        + cellSize/2
        + xform.getFacing().getX()*(cellSize/2 - 2*borderSize);
  }

  private int getYPos(P1ConnectionTransformation xform) {
    return xform.getPosition().getY()*cellSize
        + cellSize/2
        + xform.getFacing().getY()*(cellSize/2 - 2*borderSize);
  }
}