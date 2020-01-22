package ja222ts;

import graphs.*;

import java.util.*;

public class MyGraph<E> implements DirectedGraph<E> {

  private Map<E, MyNode<E>> mItems = new HashMap<>();
  private Set<Node<E>> mHeads = new HashSet<>(); // nodes with 0 predecessors
  private Set<Node<E>> mTails = new HashSet<>(); // nodes with 0 successors

  // todo: refactor

  public MyGraph() {

  }

  @Override
  public Node<E> addNodeFor(E item) {
    if (item == null) {
      throw new NullPointerException();
    }

    if (hasItem(item)) {
      return mItems.get(item);
    }

    MyNode<E> nodeItem = new MyNode<>(item);

    mItems.put(item, nodeItem);
    mHeads.add(nodeItem);
    mTails.add(nodeItem);

    return nodeItem;
  }

  @Override
  public Node<E> getNodeFor(E item) {
    if (item == null) {
      throw new NullPointerException();
    }

    if (!hasItem(item)) {
      throw new IndexOutOfBoundsException();
    }

    return mItems.get(item);
  }

  @Override
  public boolean addEdgeFor(E from, E to) {
    if (from == null || to == null)  {
      throw new NullPointerException();
    }

    // Add if necessary
    MyNode<E> src = (MyNode<E>) addNodeFor(from);
    MyNode<E> tgt = (MyNode<E>) addNodeFor(to);

    if (src.hasSucc(tgt)) // Edge already added
      return false;
    else {
      src.addSucc(tgt);
      tgt.addPred(src);
      mTails.remove(src);
      mHeads.remove(tgt);
      return true;
    }
  }

  @Override
  public boolean containsNodeFor(E item) {
    if (item == null) {
      throw new NullPointerException();
    }

    return mItems.containsKey(item);
  }

  @Override
  public int nodeCount() {
    return mItems.size();
  }

  @Override
  public Iterator<Node<E>> iterator() {
    return new NodeIterator();
  }

  @Override
  public Iterator<Node<E>> heads() {
    return mHeads.iterator();
  }

  @Override
  public int headCount() {
    return mHeads.size();
  }

  @Override
  public Iterator<Node<E>> tails() {
    return mTails.iterator();
  }

  @Override
  public int tailCount() {
    return mTails.size();
  }

  @Override
  public List<E> allItems() {
    return new LinkedList<E>(mItems.keySet());
  }

  @Override
  public int edgeCount() {
    return  mItems.values().stream()
        .mapToInt(MyNode::inDegree) // for every in there must be an out
        .reduce(0, Integer::sum);
  }

  @Override
  public void removeNodeFor(E item) {
    if (item == null) {
      throw new NullPointerException();
    }

    if (!hasItem(item)) {
      throw new IndexOutOfBoundsException();
    }

    var node = mItems.get(item);
    mItems.remove(item);
    disconnectNodeCompletely(node);
  }

  @Override
  public boolean containsEdgeFor(E from, E to) {
    if (from == null || to == null)  {
      throw new NullPointerException();
    }

    return hasItem(from) && hasItem(to)
      && mItems.get(from).hasSucc(mItems.get(to));
  }

  @Override
  public boolean removeEdgeFor(E from, E to) {
    if (from == null || to == null)  {
      throw new NullPointerException();
    }

    if (hasItem(from) && hasItem(to)) {

      var nodeFrom = mItems.get(from);
      var nodeTo = mItems.get(to);

      if (nodeFrom.hasSucc(nodeTo)) {
        nodeFrom.removeSucc(nodeTo);
        nodeTo.removePred(nodeFrom);

        checkTail(nodeFrom);
        checkTail(nodeTo);
        checkHead(nodeFrom);
        checkHead(nodeTo);

        return true;
      }
    }

    return false;
  }

  // todo: rewrite those as custom equals, compareto, etc. performance is prob. really bad. needed for requirements
  private boolean hasItem(E aItem) {
    return mItems
        .keySet()
        .stream()
        .anyMatch(x -> x.equals(aItem) && x.hashCode() == aItem.hashCode());
  }

  private boolean hasTail(Node<E> n) {
    return mTails
        .stream()
        .anyMatch(x -> x.equals(n) && x.hashCode() == n.hashCode());
  }

  private boolean hasHead(Node<E> n) {
    return mHeads
        .stream()
        .anyMatch(x -> x.equals(n) && x.hashCode() == n.hashCode());
  }

  private void disconnectNodeCompletely(MyNode<E> n) {

    // todo: make this prettier
    var nodeRelations = new LinkedList<MyNode<E>>();

    for (var iter = n.predsOf(); iter.hasNext(); ) {
      var node = iter.next();
      nodeRelations.add((MyNode<E>) node);
    }

    for (var iter = n.succsOf(); iter.hasNext(); ) {
      var node = iter.next();
      nodeRelations.add((MyNode<E>) node);
    }

    n.disconnect();
    mTails.remove(n);
    mHeads.remove(n);

    for (var node : nodeRelations) {
      checkTail(node);
      checkHead(node);
    }
  }

  // todo: these might be slow
  private void checkTail(Node<E> n) {
    if (!hasItem(n.item()) || n.outDegree() > 0) mTails.remove(n);
    else if (n.isTail() && !hasTail(n) && hasItem(n.item())) mTails.add(n);
  }

  private void checkHead(Node<E> n) {
    if (!hasItem(n.item()) || n.inDegree() > 0) mHeads.remove(n);
    else if (n.isHead() && !hasHead(n) && hasItem(n.item())) mHeads.add(n);
  }

  class NodeIterator implements Iterator<Node<E>> {

    private Iterator<MyNode<E>> nodesIter = mItems.values().iterator();

    @Override
    public boolean hasNext() {
      return nodesIter.hasNext();
    }

    @Override
    public Node<E> next() {
      return nodesIter.next();
    }
  }
}
