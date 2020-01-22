package ja222ts;

import graphs.DirectedGraph;
import graphs.Node;
import graphs.TransitiveClosure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MyTransitiveClosure<E> implements TransitiveClosure<E> {

  @Override
  public Map<Node<E>, Collection<Node<E>>> computeClosure(DirectedGraph<E> dg) {
    // slow but simple, eh? n^3 ain't THAT bad.
    var resultMap = new HashMap<Node<E>, Collection<Node<E>>>();
    var dfs = new MyDFS<E>();

    for (var node : dg) {
      resultMap.put(node, dfs.dfs(dg, node));
    }

    return resultMap;
  }
}
