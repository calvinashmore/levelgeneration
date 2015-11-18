/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phase2;

import com.google.common.collect.ImmutableSet;
import generation.ConnectionPlacement;
import generation.v3room.V3ConnectionTransformation;
import generation.v3room.V3Geometry;
import generation.v3room.renderer.ConnectionRenderer;
import generation.v3room.renderer.GraphRenderer;
import generation.v3room.renderer.RendererFactory;
import generation.v3room.renderer.VolumeRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.annotation.concurrent.Immutable;
import javax.swing.JFrame;
import javax.swing.JPanel;
import math3i.Point3i;
import math3i.Transformation3i;
import math3i.Volume3i;

/**
 *
 * @author ashmore
 */
public class Demo {
  private static final int ROOM_SIZE = 10;

  private static final P2RoomTemplate PLAINS = P2RoomTemplate.create(
          P2ConnectionTemplate.GRASS, P2ConnectionTemplate.GRASS,
          P2ConnectionTemplate.GRASS, P2ConnectionTemplate.GRASS);

  private static final P2RoomTemplate CREEK_STRAIGHT_1 = P2RoomTemplate.create(
          P2ConnectionTemplate.CREEK_IN, P2ConnectionTemplate.GRASS,
          P2ConnectionTemplate.CREEK_OUT, P2ConnectionTemplate.GRASS);

  private static final P2RoomTemplate CREEK_TO_EDGE = P2RoomTemplate.create(
          P2ConnectionTemplate.CREEK_IN, P2ConnectionTemplate.GRASS,
          P2ConnectionTemplate.NONE, P2ConnectionTemplate.GRASS);

  private static final P2RoomTemplate CREEK_STRAIGHT_2 = P2RoomTemplate.create(V3Geometry.create(Volume3i.box(2, 1, 1)), ImmutableSet.of(
          ConnectionPlacement.create(P2ConnectionTemplate.CREEK_IN, V3Geometry.connection(Point3i.ZERO, V3Geometry.WEST)),
          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.ZERO, V3Geometry.NORTH)),
          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.ZERO, V3Geometry.SOUTH)),
          ConnectionPlacement.create(P2ConnectionTemplate.CREEK_OUT, V3Geometry.connection(Point3i.create(1, 0, 0), V3Geometry.EAST)),
          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.create(1,0,0), V3Geometry.NORTH)),
          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.create(1,0,0), V3Geometry.SOUTH))
          ));

  private static final P2RoomTemplate CREEK_ELBOW = P2RoomTemplate.create(
          P2ConnectionTemplate.CREEK_IN, P2ConnectionTemplate.CREEK_OUT,
          P2ConnectionTemplate.GRASS, P2ConnectionTemplate.GRASS);

  private static final P2RoomTemplate RIVER_STRAIGHT = P2RoomTemplate.create(V3Geometry.create(Volume3i.box(2, 2, 1)), ImmutableSet.of(
          // define connections counter-clockwise starting at 0,0 west
          // note that +y is south
          ConnectionPlacement.create(P2ConnectionTemplate.RIVER_LEFT_IN, V3Geometry.connection(Point3i.ZERO, V3Geometry.WEST)),
          ConnectionPlacement.create(P2ConnectionTemplate.RIVER_RIGHT_IN, V3Geometry.connection(Point3i.create(0, 1, 0), V3Geometry.WEST)),

          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.create(0, 1, 0), V3Geometry.SOUTH)),
          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.create(1, 1, 0), V3Geometry.SOUTH)),

          ConnectionPlacement.create(P2ConnectionTemplate.RIVER_LEFT_OUT, V3Geometry.connection(Point3i.create(1, 1, 0), V3Geometry.EAST)),
          ConnectionPlacement.create(P2ConnectionTemplate.RIVER_RIGHT_OUT, V3Geometry.connection(Point3i.create(1, 0, 0), V3Geometry.EAST)),

          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.create(1, 0, 0), V3Geometry.NORTH)),
          ConnectionPlacement.create(P2ConnectionTemplate.GRASS, V3Geometry.connection(Point3i.ZERO, V3Geometry.NORTH))
          ));

  public static void main(String args[]){
    P2ContainerProgress container = new P2ContainerProgress(Volume3i.box(ROOM_SIZE, ROOM_SIZE, 1));
    FinitePrioritizedCollection<P2RoomTemplate> templates = new FinitePrioritizedCollection<>();

    // Notes for things that I'd like to be able to do:
    // Elevation, where river out must have lower level elevation
    // In this sense, we wouldn't really select *all* possibilities and choose one,
    // but rather we'd try to construct a tile rather than picking one from a bag.

//    templates.addEntries(PLAINS, 1.0, 1, 4);
//    templates.addEntries(CREEK_STRAIGHT_1, 1.0, 2, 3);
//    templates.addEntries(CREEK_STRAIGHT_2, 2.0, 2, 3);
//    templates.addEntries(CREEK_ELBOW, 1.0, 2, 3);
//    templates.addEntries(CREEK_TO_EDGE, 1.0, 2, 1);
    templates.addEntries(RIVER_STRAIGHT, 2.0, 3, 3);

//    container.addChild(P2Room.create(PLAINS,
//            V3Geometry.V3GeometryTransformation.create(Transformation3i.translation(ROOM_SIZE/2, ROOM_SIZE/2, 0))));

    container.addChild(P2Room.create(RIVER_STRAIGHT,
            V3Geometry.V3GeometryTransformation.create(Transformation3i.translation(ROOM_SIZE/2, ROOM_SIZE/2, 0))));

    P2RoomGenerator generator = new P2RoomGenerator(container, templates, new Random());

    while(!container.isValid()) {
      P2Room room = generator.generateRoom();
      if(room == null) {
        System.out.println("*** failed to generate room");
        break;
      }
      container.addChild(room);
    }
    System.out.println("added "+container.getChildren().size()+" rooms");

    renderContainer(container);
  }
//
//  public static void main(String args[]){
//    P2ContainerProgress container = new P2ContainerProgress(Volume3i.box(ROOM_SIZE, ROOM_SIZE, 1));
//    FinitePrioritizedCollection<P2RoomTemplate> templates = new FinitePrioritizedCollection<>();
//
//    templates.addEntries(PLAINS, 1.0, 1, 4);
//    templates.addEntries(CREEK_STRAIGHT_1, 1.0, 1, 3);
//    templates.addEntries(CREEK_STRAIGHT_2, 2.0, 1, 3);
//    templates.addEntries(CREEK_ELBOW, 1.0, 1, 3);
//    templates.addEntries(CREEK_TO_EDGE, 1.0, 1, 1);
//    templates.addEntries(RIVER_STRAIGHT, 2.0, 1, 3);
//
//    container.addChild(P2Room.create(PLAINS,
//            V3Geometry.V3GeometryTransformation.create(Transformation3i.translation(ROOM_SIZE/2, ROOM_SIZE/2, 0))));
//
//    P2RoomGenerator generator = new P2RoomGenerator(container, templates, new Random());
//
//    while(!container.isValid()) {
//      P2Room room = generator.generateRoom();
//      if(room == null) {
//        System.out.println("*** failed to generate room");
//        break;
//      }
//      container.addChild(room);
//    }
//    System.out.println("added "+container.getChildren().size()+" rooms");
//
//    renderContainer(container);
//  }

  private static void renderContainer(P2ContainerProgress container) {
    JFrame frame = new JFrame("wut");
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform transform = g2.getTransform();
        g2.scale(5.0/ROOM_SIZE, 5.0/ROOM_SIZE);
        container.getChildren().forEach(room -> renderRoom(g2, room));
        g2.setTransform(transform);
      }
    };

    panel.setPreferredSize(new Dimension(500, 500));

    frame.add(panel);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  private static final RendererFactory RF = new RendererFactory();

  private static void renderRoom(Graphics2D g, P2Room room) {

    RF.newVolumeRenderer().render(room.getTransformedGeometry().getVolume(), 0, g);
    room.getConnectionPlacements().stream()
            .forEach(placement -> renderConnection(g, placement));
  }

  private static void renderConnection(Graphics2D g, ConnectionPlacement<P2Room, ?> placement) {

    P2ConnectionTemplate connection = (P2ConnectionTemplate) placement.getConnection();
    ConnectionRenderer renderer;
    switch(connection) {
      case CREEK_IN:
      case CREEK_OUT:
        renderer = RF.newConnectionRenderer(new Color(.2f,.2f,.9f));
        break;
      case RIVER_LEFT_IN:
      case RIVER_RIGHT_IN:
        renderer = RF.newConnectionRenderer(new Color(.5f,.2f,.9f));
        break;
      case GRASS:
        renderer = RF.newConnectionRenderer(new Color(.1f,.1f,.1f));
        break;
        //return;
      default:
        renderer = RF.newConnectionRenderer();
    }
    renderer.render((V3ConnectionTransformation) placement.getTransform(), g);
  }
}
