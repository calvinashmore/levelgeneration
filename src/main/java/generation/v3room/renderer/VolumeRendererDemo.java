/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generation.v3room.renderer;

import com.google.common.collect.ImmutableList;
import generation.v3room.V3ConnectionTransformation;
import generation.v3room.V3Geometry;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import math3i.Point3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public class VolumeRendererDemo {
  public static void main(String args[]) {

    final Volume3i volume = new Volume3i(ImmutableList.of(
        Point3i.create(0, 0, 0),
        Point3i.create(1, 0, 0),
        Point3i.create(1, 1, 0),
        Point3i.create(2, 1, 0),
        Point3i.create(1, 2, 0),
        Point3i.create(2, 2, 0),
        Point3i.create(2, 3, 0)));

    JFrame frame = new JFrame("wut");
    JPanel panel = new JPanel() {

      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        new VolumeRenderer().render(volume, 0, g2);
        new ConnectionRenderer().render(V3ConnectionTransformation.create(Point3i.ZERO, V3Geometry.WEST), g2);
        new ConnectionRenderer().render(V3ConnectionTransformation.create(Point3i.create(2, 3, 0), V3Geometry.EAST), g2);
        new ConnectionRenderer().render(V3ConnectionTransformation.create(Point3i.create(2, 1, 0), V3Geometry.NORTH), g2);
        new ConnectionRenderer().render(V3ConnectionTransformation.create(Point3i.create(0, 0, 0), V3Geometry.SOUTH), g2);

        new VolumeRenderer().render(new Volume3i(ImmutableList.of(Point3i.create(2,0,0))), 0, g2);
        new ConnectionRenderer().render(V3ConnectionTransformation.create(Point3i.create(2, 0, 0), V3Geometry.SOUTH), g2);
      }
    };

    panel.setPreferredSize(new Dimension(500, 500));
    frame.add(panel);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
