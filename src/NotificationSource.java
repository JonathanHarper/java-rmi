import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Implementation of SourceInterface to be used for remote communication. extending UnicastRemoteObject ensures that every remote object shall be unique, therefore notifications can be directed to this particular instance.
 */
public class NotificationSource extends UnicastRemoteObject implements SourceInterface {

	private static final long serialVersionUID = 1L;
	
	// The sinks which are registered to the source
	private Vector<SinkInterface> sinksRegistered;
//	private ArrayBlockingQueue<Notification> notificationQueue; 
	//Url in which the source is connected through
	private String url;

	protected NotificationSource(String url) throws RemoteException {
		super();
		this.url = url;
//		notificationQueue = new ArrayBlockingQueue<Notification>(10);
		sinksRegistered = new Vector<SinkInterface>();
	}
	
	//Returns the source which has that url attached to it
	synchronized static protected SourceInterface getNotSource(String url) {
		try {
			Registry r = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
			SourceInterface notSource = (SourceInterface) r.lookup(url);
			return notSource;
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			System.err.println("Unable to find source with URL passed.");
			return null;
		}
	}
	
	@Override
	synchronized public void registerSink(SinkInterface sink) throws RemoteException {
		if(!sinksRegistered.contains(sink)){
			sinksRegistered.add(sink);
//			notificationQueue.add(new ArrayBlockingQueue<Notification>(20));
			System.out.println("Sink successfully registered.");
		} else {
			System.out.println("Sink registration unsuccessful! Sink is already registered to this source.");
		}
	}

	@Override
	synchronized public void unregisterSink(SinkInterface sink) throws RemoteException {
		if(sinksRegistered.contains(sink)){		
//			notificationQueue.remove(position);
			sinksRegistered.remove(sink);
			System.out.println("Sink successfully unregistered.");
		} else {
			System.out.println("Sink unregistration unsuccessful! Sink is not registered.");
		}
	}

	@Override
	synchronized public void notifySinks(Notification notification) throws RemoteException {
		
		Iterator<SinkInterface> iter = sinksRegistered.iterator();
		while (iter.hasNext()) {
			try {
				iter.next().sinkNotify(notification, this);
			} catch (RemoteException e) {
				iter.remove();
			}
		}
	}

	@Override
	synchronized public String registeredSinks() throws RemoteException {
		String s = "";
		for (SinkInterface ni : sinksRegistered) {
			s += ni + ", ";
		}
		return s;
	}

}
