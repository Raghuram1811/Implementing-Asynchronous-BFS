import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class AsynchBFS {
	private static LinkedList<Integer> Adjacency[] = null;
	private static LinkedList<Integer> Neighbor_list[] = null;

	private static int status;
	private static int[] num_neighbors = new int[15];
	private static ArrayList<child> childObjects = new ArrayList<>();
	private static HashMap<Integer, Queue<String>> Message_Map = new HashMap<>();
	private static Thread t;

	public static void main(String[] args) throws IOException {
		FileReader fr = new FileReader("in.txt");
		BufferedReader br = new BufferedReader(fr);
		String s;

		while ((!(s = br.readLine()).contains("number"))) {

		}
		int number_of_nodes = Integer.parseInt(br.readLine());

		while ((!(s = br.readLine()).contains("root"))) {

		}
		int root = Integer.parseInt(br.readLine());

		while ((!(s = br.readLine()).contains("weight"))) {

		}
		Adjacency = new LinkedList[number_of_nodes];
		Neighbor_list = new LinkedList[number_of_nodes];
		for (int i = 0; i < number_of_nodes; i++) {
			Adjacency[i] = new LinkedList();
			Neighbor_list[i] = new LinkedList();
		}

		int row_number = 0;
		String Line;
		for (int i = 0; i < num_neighbors.length; i++) {
			num_neighbors[i] = 0;
		}

		while ((Line = br.readLine()) != null) {
			Adjacency[row_number] = Tokenize(Line);
			row_number++;
		}
		// Neighbor_list=Adjacency;

		for (int i = 0; i < Adjacency.length; i++) {
			for (int j = 0; j < Adjacency[i].size(); j++) {
				if (Adjacency[i].get(j) >= 0) {
					num_neighbors[i]++;
					Neighbor_list[i].add(j);
				}
				
				// if(Adjacency[i].get(j)==-1)
			}
			if(i==0) {
			
			}
		}

		for (int i = 0; i < number_of_nodes; i++) {
			Queue<String> queue = new LinkedList<String>();
			Message_Map.put(i, queue);
		}

		for (int i = 0; i < number_of_nodes; i++) {
			if (i == root) {
				status = 1;
			} else {
				status = 0;
			}
			child c = new child(i, num_neighbors[i], Adjacency[i], status, Neighbor_list[i], Message_Map);
			t = new Thread(c);
			t.start();
			childObjects.add(c);
			row_number++;
		}

		int currentRound = 0;
		while (true) {
			for (int i = 0; i < childObjects.size(); i++) {
				child c = childObjects.get(i);
				while (c.getRoundNumber() != currentRound) {

				}
			}

			for (int i = 0; i < childObjects.size(); i++) {
				child c = childObjects.get(i);
				c.setRoundFlag(true);
			}
			currentRound++;
		}
	}

	public static LinkedList<Integer> Tokenize(String s) {
		LinkedList<Integer> l = new LinkedList<>();
		StringTokenizer st = new StringTokenizer(s, " ");
		while (st.hasMoreTokens()) {
			l.add(Integer.parseInt(st.nextToken()));
		}
		return l;
	}

}
