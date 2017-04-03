import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class child implements Runnable {
	int num_of_neighbors = 0;
	int roundNumber = 0;
	boolean roundNumberFlag = false;
	LinkedList<Integer> Adjacency = new LinkedList<>();
	LinkedList<Integer> Neighbor_list = new LinkedList<>();
	LinkedList<Integer> Child_list = new LinkedList<>();
	int node_number = 0;
	HashMap<Integer, Queue<String>> MessageMap;
	HashMap<Integer, Queue<String>> Buffer_list = new HashMap<>();
	boolean leader_flag = false;
	boolean child_flag = false;
	int done_counter = 0;

	HashMap<Integer, Boolean> DoneMap = new HashMap<>();
	Queue<String> q;

	int parent = 999;
	int hop_count_current = 999;
	int prev_parent = 999;

	int counter = 0;
	int status;
	int root;

	child(int row_no, int num_neigh, LinkedList<Integer> Adj, int stat, LinkedList<Integer> Neighbor,
			HashMap<Integer, Queue<String>> Msg_map) {
		this.node_number = row_no;
		this.num_of_neighbors = num_neigh;
		this.Adjacency = Adj;
		this.status = stat;
		this.Neighbor_list = Neighbor;
		this.MessageMap = Msg_map;

		for (int i = 0; i < Neighbor_list.size(); i++) {
			DoneMap.put(Neighbor_list.get(i), false);
		}
		
		if (status == 1) {
			root = node_number;
		}

		for (int i = 0; i < Neighbor.size(); i++) {
			Queue<String> q = new LinkedList<String>();
			Buffer_list.put(Neighbor.get(i), q);
		}
		Child_list = Neighbor;
		
	}

	int rand;
	int timeStamp;

	public void run() {

		while (true) {
			while (!getFlagStatus()) {
				// If root
				if (status == 1) {
					if (leader_flag == false) {
						for (int i = 0; i < Neighbor_list.size(); i++) {
							timeStamp = generateRandomNumber();
							Queue<String> q = Buffer_list.get(Neighbor_list.get(i));
							q.add("E" + "#" + root + "$" + "0" + "@" + root + "*" + timeStamp);
						}
						leader_flag = true;
					}
					for (int i = 0; i < Buffer_list.size(); i++) {
						if (!Buffer_list.get(Neighbor_list.get(i)).isEmpty()) {
							String Message = Buffer_list.get(Neighbor_list.get(i)).peek();
							if (roundNumber == Integer.parseInt(Message.substring(Message.indexOf('*') + 1))) {

								Buffer_list.get(Neighbor_list.get(i)).poll();
								Queue<String> q = getQueueFromMessageMap(Neighbor_list.get(i));
								AddToMessageMap(q, Message);
							}
						}
					}
					// System.out.println("Messagemap: " +MessageMap);
					Queue<String> q = getQueueFromMessageMap(root);

					while (!q.isEmpty()) {
						String Msg = q.peek();

						if (Msg == null) {
							break;
						} else {
							if (Msg.charAt(0) == 'R') {
								q.poll();
							}

							// Termination at root
							if (Msg.charAt(0) == 'D') {
								Msg = q.poll();
								DoneMap.put(Integer.parseInt(Msg.substring(Msg.indexOf('#') + 1)), true);
							}
							counter = 0;
							for (int i : DoneMap.keySet()) {
								if (DoneMap.get(i) == true) {
									counter++;
								} else {
									break;
								}
							}
							if (counter == DoneMap.size()) {

								System.out.println("Root Children: " + Child_list);
								System.exit(0);
							}
						}
					}
				}

				// Non-root
				else {
					// If there are any new messages to be sent, send them

					for (int i = 0; i < Neighbor_list.size(); i++) {
						if (!Buffer_list.get(Neighbor_list.get(i)).isEmpty() && Neighbor_list.get(i) != parent) {

							String Message = Buffer_list.get(Neighbor_list.get(i)).peek();

							if (roundNumber >= Integer.parseInt(Message.substring(Message.indexOf('*') + 1))) {
								Buffer_list.get(Neighbor_list.get(i)).poll();
								Queue<String> m = getQueueFromMessageMap(Neighbor_list.get(i));
								AddToMessageMap(m, Message);
							}
						}
					}

					// Processing Incoming Messages
					Queue<String> q = getQueueFromMessageMap(node_number);

					while (!q.isEmpty()) {
						String Message = q.peek();
						if (Message == null) {
							continue;
						}
						if (Message.charAt(0) == 'E') {
							child_flag = true;

							// Reading explore messageo
							Message = q.poll();
							int sender = Integer
									.parseInt(Message.substring(Message.indexOf('#') + 1, Message.indexOf('$')));
							int hop_count_sender = Integer
									.parseInt(Message.substring(Message.indexOf('$') + 1, Message.indexOf('@')));
							root = Integer.parseInt(Message.substring(Message.indexOf('@') + 1, Message.indexOf('*')));

							// Sending Explore messages
							if (Neighbor_list.contains(root) && sender != root) {

								parent = root;
								hop_count_current = 1;
								// Send reject to Sender
								Queue<String> m = getQueueFromMessageMap(sender);
								AddToMessageMap(m, "R" + "#" + node_number);

								Child_list.remove(Integer.valueOf(parent));

								DoneMap.remove(Integer.valueOf(parent));
								// Send Explore to remaining neighbors
								for (int i = 0; i < Neighbor_list.size(); i++) {
									if (Neighbor_list.get(i) != parent && Neighbor_list.get(i) != sender) {
										timeStamp = roundNumber + generateRandomNumber();
										Queue<String> p = Buffer_list.get(Neighbor_list.get(i));
										p.add("E" + "#" + node_number + "$" + hop_count_current + "@" + root + "*"
												+ timeStamp);
									}
								}

							} else if (hop_count_sender + 1 < hop_count_current) {

								parent = sender;
								Child_list.remove(Integer.valueOf(parent));

								DoneMap.remove(Integer.valueOf(parent));

								hop_count_current = hop_count_sender + 1;
								for (int i = 0; i < Neighbor_list.size(); i++) {
									if (Neighbor_list.get(i) != parent) {
										timeStamp = roundNumber + generateRandomNumber();
										Queue<String> p = Buffer_list.get(Neighbor_list.get(i));
										p.add("E" + "#" + node_number + "$" + hop_count_current + "@" + root + "*"
												+ timeStamp);
									}
								}

							} else {

								// Send reject to sender
								if (sender != root) {
									Queue<String> m = getQueueFromMessageMap(sender);
									AddToMessageMap(m, "R" + "#" + node_number);
									Child_list.remove(Integer.valueOf(sender));

									DoneMap.remove(Integer.valueOf(sender));
								}
							}

						}

						// Processing Reject Message
						else if (Message.charAt(0) == 'R') {
							Message = q.poll();

							Integer sender = Integer.parseInt(Message.substring(2));
							if (Child_list.contains(sender)) {

								Child_list.remove(sender);
								DoneMap.remove(sender);

							}
						}

						// Termination
						else if (Message.charAt(0) == 'D') {
							Message = q.poll();
							Integer sender = Integer.parseInt(Message.substring(2));
							DoneMap.put(sender, true);

						}
					}

					// Sending Done Messages
					// Leaf nodes
					if (child_flag == true && Child_list.size() == 0) {

						Queue<String> m = getQueueFromMessageMap(parent);
						AddToMessageMap(m, "D" + "#" + node_number);
						System.out.println("Process: " +node_number + " Parent: "+parent );
						System.out.println("Process: " + node_number + " Children" + Child_list);
						return;
					}

					// Non-root nodes
					counter = 0;
					for (int i : DoneMap.keySet()) {
						if (DoneMap.get(i) == true) {
							counter++;

						} else {
							break;
						}
					}
	
					if (counter == DoneMap.size()) {
						Queue<String> m = getQueueFromMessageMap(parent);
						AddToMessageMap(m, "D" + "#" + node_number);
						System.out.println("Process: " +node_number + " Parent: "+parent );
						System.out.println("Process " + node_number + " Children" + Child_list);
						return;

					}

				}

				setRoundFlag(false);
				incrRoundNumber();

			}

		}
	}

	public synchronized int getRoundNumber() {
		return this.roundNumber;
	}

	public synchronized void setRoundFlag(boolean stat) {
		this.roundNumberFlag = stat;
	}

	public synchronized int generateRandomNumber() {

		Random r = new Random();
		return r.nextInt(18) + 1;
	}

	public synchronized boolean getFlagStatus() {
		return this.roundNumberFlag;
	}

	public synchronized void incrRoundNumber() {
		this.roundNumber++;
	}

	public synchronized Queue<String> getQueueFromMessageMap(int node_num) {
		return this.MessageMap.get(node_num);
	}

	public synchronized void AddToMessageMap(Queue<String> queue, String Message) {
		queue.add(Message);
	}

}
