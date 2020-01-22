package ja222ts;

import graphs.ConnectedComponents;
import graphs.DirectedGraph;
import graphs.Node;

import java.util.Collection;
import java.util.LinkedList;

public class MyConnectedComponents<E> implements ConnectedComponents<E> {

  // trash solution. it works but that's about it.
  // got it down to n^3 at least. will have to do for now.
  // there's a lot of mutation going on here unfortunately

  @Override
  public Collection<Collection<Node<E>>> computeComponents(DirectedGraph<E> dg) {

    var nodeList = new LinkedList<Collection<Node<E>>>();

    for (var node : dg) {

      var listToAddTo = getListForNode(nodeList, node);
      listToAddTo.add(node);
    }

    return nodeList;
  }

  private Collection<Node<E>> getListForNode(LinkedList<Collection<Node<E>>> aList, Node<E> aNode) {

    for (var nodeList : aList) {

      if (nodeList.stream().anyMatch(x -> aNode.hasSucc(x) || aNode.hasPred(x))) {
        return nodeList;
      }
    }

    var newNodeList = new LinkedList<Node<E>>();
    aList.add(newNodeList);

    return newNodeList;
  }
}
