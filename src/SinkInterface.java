
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Implemented by NotificationSink to be used for remote communication
 * @author Jonathan Harper
 */
public interface SinkInterface extends Remote {
	
	/**
	 * Sends the Notification from the source to the sink
	 * 
	 * @param notification Notification object to be sent
	 * @param ni The source remote object to be sent from
	 */
	public void sinkNotify(Notification notification, SourceInterface ni) throws RemoteException;
}