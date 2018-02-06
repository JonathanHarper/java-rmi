
//Notifies when a sink receives a Notification
public interface NotificationReceiver {

	//Gets the notification which is sent to the sink
	public void getNotification(Notification notification, SourceInterface ns);
	
}
