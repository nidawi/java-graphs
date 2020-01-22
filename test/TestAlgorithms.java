package graphs.test;

import graphs.ConnectedComponents;
import graphs.DFS;
import graphs.DirectedGraph;
import graphs.Node;
import graphs.TransitiveClosure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import ja222ts.*;               // Replace with a package (named after your LNU user name, e.g. na222fx) that contains your implementation of the DirectedGraph or other used interfaces / abstract classes;

public class TestAlgorithms {
	// Run test as a standard Java application
	public static void main(String[] args) {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out)); 
		junit.run(TestAlgorithms.class);
	}

	/* ************************************************
	 *      Test suite setup
	 * ************************************************/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final GraphGenerator generator = new GraphGenerator(new MyGraph());

	@Before
	public void setUp() throws Exception {
		System.out.println("\t"+this.getClass().getName());
	}

	@After
	public void tearDown() { }


	/* ************************************************
	 *      Tests
	 * ************************************************/
	@Test
	public void testTransitiveClosure() throws Exception {
		DirectedGraph<Integer> dg = generator.getTwoParts();
		Integer[] i = generator.getUsedItems();
		
		TransitiveClosure<Integer> tc = new MyTransitiveClosure<Integer>();
		Map<Node<Integer>,Collection<Node<Integer>>> map = tc.computeClosure(dg);
		
		/* Compare with manual results */
		Node<Integer> n = dg.getNodeFor(i[0]);
		Collection<Node<Integer>> c = map.get(n);
		Collection<Node<Integer>> r = nodes(dg,new int[]{0,1,2,3,4,5,6});
		assertEquals(r.size(),c.size());
		assertTrue(r.containsAll(c));
		
		n = dg.getNodeFor(i[10]);
		c = map.get(n);
		r = nodes(dg,new int[]{10,11,12,13,14,15,16});
		assertEquals(r.size(),c.size());
		assertTrue(r.containsAll(c));
		
		n = dg.getNodeFor(i[4]);
		c = map.get(n);
		r = nodes(dg,new int[]{1,3,4,5,6});
		assertEquals(r.size(),c.size());
		assertTrue(r.containsAll(c));
		
		n = dg.getNodeFor(i[13]);
		c = map.get(n);
		r = nodes(dg,new int[]{13,14,15,16});
		assertEquals(r.size(),c.size());
		assertTrue(r.containsAll(c));
		
		/* Check simple facts */
		check_closure(dg);
		
		dg = generator.getBinaryTree(4);
		check_closure(dg);
    	
    	dg = generator.getComplete(4);
		check_closure(dg);
		
		dg = generator.getRandom(100,0.01);
		check_closure(dg);	
	}
	
	@Test
	public void testConnectedComponents() throws Exception {
		ConnectedComponents<Integer> mycc = new MyConnectedComponents<Integer>();
//		Integer[] i = generator.getUsedItems();
		
		DirectedGraph<Integer> dg = generator.getDisconnected();
		
		Collection<Collection<Node<Integer>>> cc = mycc.computeComponents(dg);
		
		/* Compare with manual results */
		assertEquals(4,cc.size());
		Collection<Node<Integer>> r;
		for (Collection<Node<Integer>> c: cc) {
			if (c.size() == 4) {
				r = nodes(dg,new int[]{0,1,2,3});
				assertTrue(r.containsAll(c));
			}
			else if (c.size() == 3) {
				r = nodes(dg,new int[]{4,5,6});
				assertTrue(r.containsAll(c));
			}
			else if (c.size() == 2) {
				r = nodes(dg,new int[]{7,8});
				assertTrue(r.containsAll(c));
			}
			else if (c.size() == 1) {
				int n = c.iterator().next().item();
				assertTrue(n==9);
			}
			else
				assertTrue(false);
		}
		
		/* Check simple facts */
		check_partitioning(dg,cc);
		
		dg = generator.getCyclic();
		cc = mycc.computeComponents(dg);
		assertEquals(1,cc.size());
		Iterator<Collection<Node<Integer>>> it = cc.iterator();
		assertEquals(12,it.next().size());
		check_partitioning(dg,cc);
		
		dg = generator.getTwoParts();
		cc = mycc.computeComponents(dg);
		assertEquals(2,cc.size());
		it = cc.iterator();
		assertEquals(7,it.next().size());
		assertEquals(7,it.next().size());
		check_partitioning(dg,cc);

		dg = generator.getComplete(10);
		cc = mycc.computeComponents(dg);
		assertEquals(1,cc.size());
		it = cc.iterator();
		assertEquals(10,it.next().size());
		check_partitioning(dg,cc);
	}
	
	
	
	private void check_partitioning(DirectedGraph<Integer> dg,
			                        Collection<Collection<Node<Integer>>> cc) {
		int sz = 0;
		HashSet<Node<Integer>> count = new HashSet<Node<Integer>>();
		for (Collection<Node<Integer>> c: cc) {
			assertFalse(c.size()==0);
			sz += c.size();
			count.addAll(c);
		}
		assertEquals(dg.nodeCount(),sz);
		assertEquals(dg.nodeCount(),count.size());
	}

	private void check_closure(DirectedGraph<Integer> dg) throws Exception {
		TransitiveClosure<Integer> tc = new MyTransitiveClosure<Integer>();

		DFS<Integer> dfs = new MyDFS<Integer>();

		Map<Node<Integer>,Collection<Node<Integer>>> map = tc.computeClosure(dg);
		assertEquals(map.size(),dg.nodeCount());
		
		Iterator<Node<Integer>> it = map.keySet().iterator();
		while (it.hasNext()) {
			Node<Integer> n = it.next();
			Collection<Node<Integer>> c = map.get(n);
			if (c.size() == 0)
				assertTrue(n.isTail());
			assertTrue(n.outDegree()<=c.size());
			
			/* Compare with dfs */
			Collection<Node<Integer>> r = dfs.dfs(dg,n);
			assertTrue(r.containsAll(c));
		}
	}
	
	private Collection<Node<Integer>> nodes(DirectedGraph<Integer> dg,int[] ns) {
		Integer[] i = generator.getUsedItems();
		ArrayList<Node<Integer>> list = new ArrayList<Node<Integer>>();
		for (int j=0;j<ns.length;j++) {
			list.add(dg.getNodeFor(i[ns[j]]));
		}
		return list;
	}

}
