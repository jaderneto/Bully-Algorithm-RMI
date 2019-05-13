import java.lang.management.ManagementFactory;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class Node4 implements InterfaceNode {
	
	ArrayList<Long> nodes = new ArrayList<Long>();
	static InterfaceNode stub;
	static long pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
	private static String nodeName = Long.toString(pid); 
	private static long leader;
	
	public Node4() {
		super();
	}

	public static void main(String[] args){
		// TODO Auto-generated method stub
		Node4 obj = new Node4();
		try {
			stub = (InterfaceNode) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(nodeName, stub);
			
			System.err.println("Node " + nodeName + " is UP");
			
			Thread.sleep(45000); //Wait 35 seconds before start the election
			stub.startElection(nodeName);
			
			
						
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new ShutDown());
		
		try {
			stub.coordinatorAlive(nodeName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void startElection(String node) throws RemoteException {
		// TODO Auto-generated method stub
		
		System.out.println("O node " + node + " iniciou uma eleição!");
		Registry reg = LocateRegistry.getRegistry();
		int maior = Integer.parseInt(node);
		
		for(String registeredNodes : reg.list()) {
			if (!registeredNodes.equals(node) && Integer.parseInt(registeredNodes) > maior) {
				maior = Integer.parseInt(registeredNodes);
			}
		}
		
		for(String registeredNodes : reg.list()) {
			InterfaceNode stub;
			try {
				stub = (InterfaceNode) reg.lookup(registeredNodes);
				stub.setLeader(maior);

			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}

	@Override
	public void setLeader(int newLeader) throws RemoteException {
		// TODO Auto-generated method stub
		leader = newLeader;
		System.out.println("O novo Leader é " + leader);
	}

	public void updateListNodes() throws RemoteException {
		nodes.clear();
		
		Registry reg = LocateRegistry.getRegistry();
		
		for(String registeredNodes : reg.list()) {
			nodes.add(Long.valueOf(registeredNodes));
		}	
	}
	
	
	@Override
	public boolean isalive() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void showList(){
		for(Long st : nodes) {
			System.out.print(st + " ");
		}
	}
	
	private static void coordinatorCrashed() {
		System.out.println("O coordenador caiu. Uma nova eleição será iniciada!!!");
		try {
			stub.startElection(nodeName);
			stub.coordinatorAlive(nodeName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public void coordinatorAlive(String node) throws RemoteException {
		InterfaceNode stub2;
		Registry reg = LocateRegistry.getRegistry();
			try {
				stub2 = (InterfaceNode) reg.lookup(Long.toString(leader));
				if(stub2.isalive()) {
					System.out.println("O coordenador "+ leader + " continua vivo!!!");
					Thread.sleep(15000); //Wait 15 seconds before test again
					stub.coordinatorAlive(nodeName);
				}

			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				coordinatorCrashed(); //The coordinator has crashed
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}


	static class ShutDown extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				System.out.println("Finalizando o Node");
				LocateRegistry.getRegistry().unbind(nodeName);
			} catch (AccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	/*
	public static void coordinatorCheck() {
		try {
			stub.updateListNodes();
			if(coordAlive()) {
				System.out.println("O coordenador continua vivo!!!");
				Thread.sleep(1000);
				coordinatorCheck();
			}
			else {
				System.out.println("O coordenador caiu!!! " + "O Node " + nodeName + " iniciou uma nova eleição!!!" );
				stub.startElection(nodeName);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
}
