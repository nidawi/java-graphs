package ja222ts;

import graphs.DFS;
import graphs.DirectedGraph;
import graphs.Node;

import java.util.*;

public class MyDFS<E> implements DFS<E> {

  private int mSteps;
  private HashSet<Node<E>> mVisitedNodes;

  @Override
  public List<Node<E>> dfs(DirectedGraph<E> graph, Node<E> root) {
    resetDfs(graph);
    return iterativeDfs(root);
  }

  @Override
  public List<Node<E>> dfs(DirectedGraph<E> graph) {
    resetDfs(graph);
    var resultList = new LinkedList<Node<E>>();

    for (var node : graph ) {
      // we don't need this check here due to the nature of the algorithm
      // but doing it this way means that we have one less linkedlist and arraydeque to allocate and initialize only to throw away right after.
      if (!mVisitedNodes.contains(node)) {
        var nodeTree = iterativeDfs(node);
        resultList.addAll(nodeTree);
      }
    }

    return resultList;
  }

  private void resetDfs(DirectedGraph<E> graph) {
    mSteps = 0; // reset step count
    mVisitedNodes = new HashSet<>(); // reset visited nodes
  }

  private List<Node<E>> iterativeDfs(Node<E> aNode) {
    // I implemented this: https://en.wikipedia.org/wiki/Depth-first_search
    var resultList = new LinkedList<Node<E>>();
    var nodesToProcess = new ArrayDeque<Node<E>>();
    nodesToProcess.push(aNode);

    while (!nodesToProcess.isEmpty()) {
      var currentNode = nodesToProcess.pop();
      if (!mVisitedNodes.contains(currentNode)) {
        mVisitedNodes.add(currentNode);
        currentNode.num = mSteps++;
        resultList.add(currentNode);

        for (var iter = currentNode.succsOf(); iter.hasNext(); ) {
          nodesToProcess.push(iter.next());
        }
      }
    }

    return resultList;
  }

  @Override
  public List<Node<E>> postOrder(DirectedGraph<E> g, Node<E> root) {
    resetDfs(g);
    var resultList = new LinkedList<Node<E>>();
    recursivePostOrder(root, resultList);

    return resultList;
  }

  @Override
  public List<Node<E>> postOrder(DirectedGraph<E> g) {
    resetDfs(g);

    var resultList = new LinkedList<Node<E>>();
    for (var node : g) {

      var result = new LinkedList<Node<E>>();
      recursivePostOrder(node, result);
      resultList.addAll(result);
    }

    return resultList;
  }

  private void recursivePostOrder(Node<E> aNode, List<Node<E>> aListPointer) {
    if (mVisitedNodes.contains(aNode))
      return;

    mVisitedNodes.add(aNode);

    for (var iter = aNode.succsOf(); iter.hasNext(); ) {
      var node = iter.next();
      recursivePostOrder(node, aListPointer);
    }

    aNode.num = mSteps++;
    aListPointer.add(aNode);
  }

  @Override
  public List<Node<E>> postOrder(DirectedGraph<E> g, boolean attach_dfs_number) {
    resetDfs(g);
    var postOrder = postOrder(g);

    if (attach_dfs_number) {
      dfs(g); // calling dfs mutates the objects' num-values
      // and then we just return the postorder list with dfs numbers.
    }

    return postOrder;
  }

  @Override
  public boolean isCyclic(DirectedGraph<E> graph) {
    postOrder(graph); // assign po numbers to nodes
    for (var node : graph) {

      for (var iter = node.succsOf(); iter.hasNext(); ) {

        if (node.num <= iter.next().num)
          return true;
      }
    }

    return false;
  }

  @Override
  public List<Node<E>> topSort(DirectedGraph<E> graph) {
    // I interpreted "undefined" as null, and not as an exception
    return isCyclic(graph)
        ? null
        : reverse(postOrder(graph));
  }

  private List<Node<E>> reverse(List<Node<E>> aInput) {
    Collections.reverse(aInput);
    return aInput;
  }
}
