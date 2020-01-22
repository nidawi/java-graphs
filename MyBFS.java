package ja222ts;

import graphs.BFS;
import graphs.DirectedGraph;
import graphs.Node;

import java.util.*;

public class MyBFS<E> implements BFS<E> {

  private int mSteps;
  private HashSet<Node<E>> mVisitedNodes;

  @Override
  public List<Node<E>> bfs(DirectedGraph<E> graph, Node<E> root) {
    resetBfs(graph);
    return doBfs(root);
  }

  @Override
  public List<Node<E>> bfs(DirectedGraph<E> graph) {
    resetBfs(graph);
    var resultList = new LinkedList<Node<E>>();

    for (var node : graph) {
      // due to the nature of the algorithm, we need to have a check here as well
      if (!mVisitedNodes.contains(node)) {
        resultList.addAll(doBfs(node));
      }
    }

    return resultList;
  }

  private void resetBfs(DirectedGraph<E> graph) {
    mSteps = 0; // reset step count
    mVisitedNodes = new HashSet<>();
  }

  private List<Node<E>> doBfs(Node<E> root) {
    // implemented from https://en.wikipedia.org/wiki/Breadth-first_search
    // no need to reinvent the wheel, right?
    var resultList = new LinkedList<Node<E>>();

    Queue<Node<E>> nodeQueue = new LinkedList<>();
    mVisitedNodes.add(root);
    root.num = mSteps++;
    nodeQueue.add(root);

    while (!nodeQueue.isEmpty()) {

      var currentNode = nodeQueue.poll();
      resultList.add(currentNode);

      for (var iter = currentNode.succsOf(); iter.hasNext(); ) {

        var subNode = iter.next();
        if (!mVisitedNodes.contains(subNode)) {
          mVisitedNodes.add(subNode);
          subNode.num = mSteps++;
          nodeQueue.add(subNode);
        }
      }
    }

    return resultList;
  }
}
