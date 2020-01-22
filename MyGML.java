package ja222ts;

import graphs.DirectedGraph;
import graphs.GML;
import graphs.Node;

import java.util.LinkedList;

public class MyGML<E> extends GML<E> {

  private static final String creator = "MyGML";
  private static final double version = 1.0;

  public MyGML(DirectedGraph<E> dg) {
    super(dg);
  }

  @Override
  public String toGML() {
    String graphSettings = createGraphSettings();
    String graphNodes = getNodes();
    String graphEdges = getEdges();
    return createGraphMetaData() + createElement("graph",
        graphSettings
        + graphNodes
        + graphEdges,
        0
    );
  }

  private static String createGraphMetaData() {
    String creatorLabel = createAttribute("Creator", creator, 0);
    String versionLabel = createAttribute("Version", version, 0);

    return creatorLabel + versionLabel;
  }

  private static String createGraphSettings() {
    String directedLabel = createAttribute("directed", 1, 1);
    String hierarchicLabel = createAttribute("hierarchic", 1, 1);

    return directedLabel + hierarchicLabel;
  }

  private String getNodes() {
    StringBuilder nodesBuilder = new StringBuilder();

    int id = 0;
    for (var node : super.graph) {
      id++;
      nodesBuilder.append(createNode(node, id));
      node.num = id;
    }

    return nodesBuilder.toString();
  }

  private static String createNode(Node aNode, int aId) {
    String idLabel = createAttribute("id", aId, 2);
    String nodeLabel = createAttribute("label", aNode.item().toString(), 2);

    return createElement("node", idLabel + nodeLabel, 1);
  }

  private String getEdges() {
    var relations = getRelationshipNodes();

    return relations
        .stream()
        .map(x -> createEdge(x.getKey(), x.getValue()))
        .reduce("", (x, y) -> x + y);
  }

  private static String createEdge(Node aSrc, Node aTgt) {
    String sourceLabel = createAttribute("source", aSrc.num, 2);
    String targetLabel = createAttribute("target", aTgt.num, 2);

    return createElement("edge", sourceLabel + targetLabel, 1);
  }

  private static String createAttribute(String aKey, Object aValue, int depth) {
    return String.format("%s%s %s%s", createIndention(depth), aKey, valueToString(aValue), System.lineSeparator());
  }

  private static String valueToString(Object aValue) {
    // if string: escape it, if number: leave it alone, anything else: toString and escape whatever we get back.

    if (aValue instanceof String)
      return String.format("\"%s\"", aValue);
    else if (aValue instanceof Number)
      return String.valueOf(aValue);
    else
      return String.format("\"%s\"", aValue.toString());
  }

  private static String createElement(String aKey, String aValue, int depth) {
    return String.format("%s%s [%s%s%s]%s",
        createIndention(depth),
        aKey,
        System.lineSeparator(),
        aValue,
        createIndention(depth),
        System.lineSeparator());
  }

  private static String createIndention(int depth) {
    return "  ".repeat(depth);
  }

  private LinkedList<Pair<Node<E>, Node<E>>> getRelationshipNodes() {
    // perhaps not the prettiest. worst case of O(n^2) for this method alone.
    var relevantNodes = new LinkedList<Pair<Node<E>, Node<E>>>();

    for (var node : super.graph) {

      // Store all succs.
      for (var succs = node.succsOf(); succs.hasNext(); ) {
        relevantNodes.add(new Pair<>(node, succs.next()));
      }
    }

    return relevantNodes;
  }

  // The pair class of javafx doesn't exist anymore, apparently (in JDK12).
  static class Pair<K, V> {
    private K mKey;
    private V mValue;

    public K getKey() {
      return mKey;
    }

    public V getValue() {
      return mValue;
    }

    public Pair(K aKey, V aValue) {
      mKey = aKey;
      mValue = aValue;
    }
  }
}