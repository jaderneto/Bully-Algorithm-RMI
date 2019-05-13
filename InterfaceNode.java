import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceNode extends Remote{
	
	public void startElection(String nodeName) throws RemoteException;
	
	public void setLeader(int newLeader) throws RemoteException;
	
	public boolean isalive() throws RemoteException;
	
	public void updateListNodes() throws RemoteException;
	
	public void coordinatorAlive(String node) throws RemoteException;

}
