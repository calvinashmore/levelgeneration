/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room.renderer;

import generation.ConnectionPlacement;
import generation.KeyType;
import generation.Room;
import generation.v3room.V3ConnectionTransformation;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
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

  public <T extends Room<T,?,K>, K extends KeyType> void render(Graph<ConnectionPlacement<T, K>, K> graph, Graphics2D g) {

    for (ConnectionPlacement<T, K> placement1 : graph.getAllNodes()) {
      for (ConnectionPlacement<T, K> placement2 : graph.getConnections(placement1).keySet()) {
        V3ConnectionTransformation transform1 = (V3ConnectionTransformation) placement1.getTransform();
        V3ConnectionTransformation transform2 = (V3ConnectionTransformation) placement2.getTransform();

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

  private int getXPos(V3ConnectionTransformation xform) {
    return xform.getPosition().getX()*cellSize
        + cellSize/2
        + xform.getFacing().getX()*(cellSize/2 - 2*borderSize);
  }

  private int getYPos(V3ConnectionTransformation xform) {
    return xform.getPosition().getY()*cellSize
        + cellSize/2
        + xform.getFacing().getY()*(cellSize/2 - 2*borderSize);
  }
}
