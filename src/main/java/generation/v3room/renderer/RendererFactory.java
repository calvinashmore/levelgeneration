/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room.renderer;

import java.awt.Color;

/**
 *
 * @author ashmore
 */
public class RendererFactory {
  private final int cellSize;
  private final int borderSize;

  private int lineWidth = 3;

  public RendererFactory() {
    this(100, 10);
  }

  public RendererFactory(int cellSize, int borderSize) {
    this.cellSize = cellSize;
    this.borderSize = borderSize;
  }

  public void setLineWidth(int lineWidth) {
    this.lineWidth = lineWidth;
  }

  public GraphRenderer newGraphRenderer(Color color) {
    return new GraphRenderer(cellSize, borderSize, lineWidth, color);
  }

  public GraphRenderer newGraphRenderer() {
    return newGraphRenderer(Color.GRAY);
  }

  public ConnectionRenderer newConnectionRenderer(Color color) {
    return new ConnectionRenderer(cellSize, borderSize, lineWidth, color);
  }

  public ConnectionRenderer newConnectionRenderer() {
    return newConnectionRenderer(Color.BLUE);
  }

  public VolumeRenderer newVolumeRenderer(Color color) {
    return new VolumeRenderer(cellSize, borderSize, lineWidth, color);
  }

  public VolumeRenderer newVolumeRenderer() {
    return newVolumeRenderer(Color.BLACK);
  }
}
