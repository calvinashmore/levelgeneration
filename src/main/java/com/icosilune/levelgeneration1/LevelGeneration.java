/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icosilune.levelgeneration1;

import com.google.common.collect.ImmutableList;
import generation.v3room.V3ConnectionTransformation;
import generation.v3room.V3Geometry;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import math3i.Point3i;
import math3i.Volume3i;
import phase1.P1ConnectionTemplate;
import phase1.P1ContainerProgress;
import phase1.P1Room;
import phase1.P1RoomGenerator;
import phase1.P1RoomTemplate;
import phase1.P1RoomTemplateGenerator;
import generation.v3room.renderer.ConnectionRenderer;
import generation.v3room.renderer.GraphRenderer;
import generation.v3room.renderer.VolumeRenderer;
import util.PrioritizedCollection;

/**
 *
 * @author ashmore
 */
public class LevelGeneration {

  private static final int ROOM_SIZE = 9;

  private static final P1RoomTemplateGenerator SINGLE_CELL_ROOM = new P1RoomTemplateGenerator(V3Geometry.create(Volume3i.box(1, 1, 1)))
          .addConnections(V3Geometry.connection(Point3i.ZERO, V3Geometry.EAST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.ZERO, V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.ZERO, V3Geometry.WEST), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final P1RoomTemplateGenerator BIG_ROOM = new P1RoomTemplateGenerator(V3Geometry.create(Volume3i.box(3, 3, 1)))
          .addConnections(V3Geometry.connection(Point3i.create(0,1,0), V3Geometry.WEST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(2,1,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(2,2,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(1,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(1,2,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final P1RoomTemplateGenerator HALLWAY1 = new P1RoomTemplateGenerator(V3Geometry.create(Volume3i.box(7, 1, 1)))
          .addConnections(V3Geometry.connection(Point3i.create(0,0,0), V3Geometry.WEST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(6,0,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(4,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(4,0,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final P1RoomTemplateGenerator TINY_HALLWAY1 = new P1RoomTemplateGenerator(V3Geometry.create(Volume3i.box(3, 1, 1)))
          .addConnections(V3Geometry.connection(Point3i.create(0,0,0), V3Geometry.WEST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(1,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(1,0,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final P1RoomTemplateGenerator TINY_HALLWAY2 = new P1RoomTemplateGenerator(V3Geometry.create(Volume3i.box(5, 1, 1)))
          .addConnections(V3Geometry.connection(Point3i.create(0,0,0), V3Geometry.WEST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(4,0,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final P1RoomTemplateGenerator L_HALLWAY1 = new P1RoomTemplateGenerator(V3Geometry.create(new Volume3i( ImmutableList.of(
                Point3i.create(0, 0, 0),
                Point3i.create(1, 0, 0),
                Point3i.create(2, 0, 0),
                Point3i.create(3, 0, 0),
                Point3i.create(3, 1, 0)))))
          .addConnections(V3Geometry.connection(Point3i.create(0,0,0), V3Geometry.WEST), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(3,0,0), V3Geometry.EAST), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(3,1,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL)
          .addConnections(V3Geometry.connection(Point3i.create(2,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final P1RoomTemplateGenerator U_HALLWAY1 = new P1RoomTemplateGenerator(V3Geometry.create(new Volume3i( ImmutableList.of(
                Point3i.create(0, 0, 0),
                Point3i.create(0, 1, 0),
                Point3i.create(1, 0, 0),
                Point3i.create(2, 0, 0),
                Point3i.create(2, 1, 0)))))
          .addConnections(V3Geometry.connection(Point3i.create(0,1,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(2,1,0), V3Geometry.SOUTH), P1ConnectionTemplate.DOOR_1)
          .addConnections(V3Geometry.connection(Point3i.create(1,0,0), V3Geometry.NORTH), P1ConnectionTemplate.DOOR_1, P1ConnectionTemplate.WALL);

  private static final Random random = new Random();

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    P1ContainerProgress container = new P1ContainerProgress(Volume3i.box(ROOM_SIZE, ROOM_SIZE, 1));
    container.addParentConnection(P1ConnectionTemplate.placement(
            P1ConnectionTemplate.DOOR_1, Point3i.create(0,3,0), V3Geometry.WEST));
    container.addParentConnection(P1ConnectionTemplate.placement(
            P1ConnectionTemplate.DOOR_1, Point3i.create(ROOM_SIZE-1,3,0), V3Geometry.EAST));

    container.addParentConnection(P1ConnectionTemplate.placement(
            P1ConnectionTemplate.DOOR_1, Point3i.create(0,3,0), V3Geometry.WEST));

    PrioritizedCollection<P1RoomTemplate> templates = new PrioritizedCollection<>();
    templates.addEntries(SINGLE_CELL_ROOM.generateTemplates(),
            template -> 0.5/template.getNumberOfDoors(), 1, 1);
    templates.addEntries(BIG_ROOM.generateTemplates(),
            template -> template.getNumberOfDoors() == 2 ? 1.0 : 0.5, 1, 1);
    templates.addEntries(HALLWAY1.generateTemplates(), 1.1, 1);
    templates.addEntries(TINY_HALLWAY1.generateTemplates(), 1, 1);
    templates.addEntries(TINY_HALLWAY2.generateTemplates(), 1, 1);
    templates.addEntries(L_HALLWAY1.generateTemplates(), 1.2, 1);
    templates.addEntries(U_HALLWAY1.generateTemplates(), 1.5, 1);

    P1RoomGenerator generator = new P1RoomGenerator(container, templates, random);

    while(!container.isValid()) {
      P1Room room = generator.generateRoom();
      if(room == null) {
        System.out.println("*** failed to generate room");
        break;
      }
      container.addChild(room);
    }
    System.out.println("added "+container.getChildren().size()+" rooms");

    renderContainer(container);
  }

  private static void renderContainer(P1ContainerProgress container) {

    JFrame frame = new JFrame("wut");
    JPanel panel = new JPanel() {

      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform transform = g2.getTransform();
        g2.scale(5.0/ROOM_SIZE, 5.0/ROOM_SIZE);
        container.getChildren().forEach(room -> renderRoom(g2, room));
        GRAPH_RENDERER.render(container.buildConnectionGraph(), g2);
        g2.setTransform(transform);
      }
    };

    panel.setPreferredSize(new Dimension(500, 500));

    frame.add(panel);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  private static final VolumeRenderer VOLUME_RENDERER = new VolumeRenderer();
  private static final ConnectionRenderer CONNECTION_RENDERER = new ConnectionRenderer();
  private static final GraphRenderer GRAPH_RENDERER = new GraphRenderer();

  private static void renderRoom(Graphics2D g, P1Room room) {

    VOLUME_RENDERER.render(room.getTransformedGeometry().getVolume(), 0, g);
    room.getConnectionPlacements().stream()
            .filter((generation.ConnectionPlacement<phase1.P1Room, phase1.P1KeyType> placement) -> placement.getConnection() != P1ConnectionTemplate.WALL)
            .forEach((generation.ConnectionPlacement<phase1.P1Room, phase1.P1KeyType> placement) -> {
      CONNECTION_RENDERER.render((V3ConnectionTransformation) placement.getTransform(), g);
    });
  }
}
