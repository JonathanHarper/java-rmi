
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Implemented by NotificationSource to be used for remote communication
 * @author Jonathan Harper
 */
public interface SourceInterface extends Remote {
	/**
	 * Notifies the sinks which are currently registered to the source with a Notification
	 */
	public void notifySinks(Notification notification) throws RemoteException;
	
	/**
	 * Registers the sink to the source so that it is able to recieve Notification's
	 */
	public void registerSink(SinkInterface sink) throws RemoteException;
	/**
	 * Unregisters the sink from the source
	 */
	public void unregisterSink(SinkInterface sink) throws RemoteException;
	
	/**
	 * List of the registered sinks of a source in a text format
	 */
	public String registeredSinks() throws RemoteException;
}