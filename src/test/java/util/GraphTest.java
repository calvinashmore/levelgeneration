/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author ashmore
 */
@RunWith(JUnit4.class)
public class GraphTest {

  @Test
  public void testCreateSaturatedGraph() {
    Graph<Integer, String> saturatedGraph = Graph.createSaturatedGraph(ImmutableList.of(1,2,3), "");
    Truth.assertThat(saturatedGraph.getAllNodes()).containsExactly(1,2,3);
    Truth.assertThat(saturatedGraph.getConnections(1).keySet()).containsExactly(2,3);
    Truth.assertThat(saturatedGraph.getConnections(2).keySet()).containsExactly(1,3);
    Truth.assertThat(saturatedGraph.getConnections(3).keySet()).containsExactly(1,2);
  }

  @Test
  public void testGraphTransformNodes() {
    Graph<Integer, String> saturatedGraph = Graph.createSaturatedGraph(ImmutableList.of(1,2,3), "");
    saturatedGraph = saturatedGraph.transformNodes(n -> n*2);
    Truth.assertThat(saturatedGraph.getAllNodes()).containsExactly(2,4,6);
    Truth.assertThat(saturatedGraph.getConnections(2).keySet()).containsExactly(4,6);
  }
}
