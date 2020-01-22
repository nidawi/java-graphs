package ja222ts;

import graphs.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MyNode<E> extends Node<E> {

  private Set<Node<E>> mPreds = new HashSet<>();
  private Set<Node<E>> mSuccs = new HashSet<>();

  /**
   * Constructs a new node using <tt>item</tt> as key.
   *
   * @param item
   */
  protected MyNode(E item) {
    super(item);
  }

  @Override
  public boolean hasSucc(Node<E> node) {
    return mSuccs.contains(node);
  }

  @Override
  public int outDegree() {
    // number of edges out of the node
    return mSuccs.size();
  }

  @Override
  public Iterator<Node<E>> succsOf() {
    return mSuccs.iterator();
  }

  @Override
  public boolean hasPred(Node<E> node) {
    return mPreds.contains(node);
  }

  @Override
  public int inDegree() {
    // number of edges into the node
    return mPreds.size();
  }

  @Override
  public Iterator<Node<E>> predsOf() {
    return mPreds.iterator();
  }

  @Override
  protected void addSucc(Node<E> succ) {
    mSuccs.add(succ);
  }

  @Override
  protected void removeSucc(Node<E> succ) {
    mSuccs.remove(succ);
  }

  @Override
  protected void addPred(Node<E> pred) {
    mPreds.add(pred);
  }

  @Override
  protected void removePred(Node<E> pred) {
    mPreds.remove(pred);
  }

  @Override
  protected void disconnect() {

    for (var v : mSuccs) {
      ((MyNode<E>) v).removePred(this);
    }

    for (var v : mPreds) {
      ((MyNode<E>) v).removeSucc(this);
    }

    mSuccs.clear();
    mPreds.clear();
  }
}
