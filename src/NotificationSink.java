import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of SinkInterface to be used for remote communication.
 * @author Jonathan Harper
 */
public class NotificationSink extends UnicastRemoteObject implements SinkInterface {

	private static final long serialVersionUID = 1L;
	private final NotificationReceiver nr;
	
	protected NotificationSink(NotificationReceiver receiver) throws RemoteException {
		super();
		nr = receiver;
	}

	@Override
	synchronized public void sinkNotify(Notification notification, SourceInterface ns) throws RemoteException {
		nr.getNotification(notification, ns);
	}
}
