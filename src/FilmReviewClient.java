import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Client for the film review application
 * @author Jonathan Harper
 */
public class FilmReviewClient {

	public static void main(String[] args) {
		ClientGUI frame = new ClientGUI("Film Reviews");
		frame.init();
	}
}

// Creates the GUI for the client application
class ClientGUI extends JFrame implements NotificationReceiver {

	private static final long serialVersionUID = 1L;
	private Vector<FilmReview> filmReviews;
	// Holds the urls and the sources connected to them
	private HashMap<String, SourceInterface> urlSourceHash = new HashMap<String, SourceInterface>();

	private JList reviewList;
	private JList sourceList;
	private DefaultListModel sourceModel;
	private DefaultListModel reviewModel;

	private SinkInterface sink;

	protected ClientGUI(String title) {
		super(title);
	}

	protected void init() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		addComponents();
		filmReviews = new Vector<FilmReview>();

		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	// Both the review panel and the connected-sources panel
	protected JPanel southPanel() {
		JPanel panel = new JPanel();

		panel.setLayout(new FlowLayout());

		reviewList = new JList();
		reviewModel = new DefaultListModel<>();
		JScrollPane reviewsPane = new JScrollPane(reviewList);
		reviewsPane.setPreferredSize(new Dimension(500, 500));

		TitledBorder reviewBorder = new TitledBorder("Film Reviews");
		reviewsPane.setBorder(reviewBorder);

		sourceList = new JList();
		sourceModel = new DefaultListModel<>();
		JScrollPane sourcePane = new JScrollPane(sourceList);
		sourcePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sourcePane.setPreferredSize(new Dimension(250, 500));

		reviewBorder = new TitledBorder("Connected Sources");
		sourcePane.setBorder(reviewBorder);

		panel.add(sourcePane);
		panel.add(reviewsPane);

		pack();

		return panel;
	}

	// Subscription to specified url
	protected JPanel northPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JTextField urlInput = new JTextField("Input URL to connect", 20);
		JButton subscribe = new JButton("Subscribe");

		subscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If source has not already been added add the source
				if (!urlSourceHash.containsKey(urlInput.getText())) {
					subscribeSource(urlInput.getText());
				}
			}
		});

		panel.add(urlInput);
		panel.add(subscribe);

		pack();
		return panel;
	}

	@Override
	// When a notification is received the film is added to the list
	public void getNotification(Notification notification, SourceInterface ns) {
		if (notification instanceof FilmReview) {
			FilmReview fr = (FilmReview) notification;
			filmReviews.add(fr);

			String review = "<html>Critic name: " + fr.getCriticName() + " - URL " + fr.getUrl() + "<br>Film title: "
					+ fr.getFilmTitle() + "<br>Review: " + fr.getReview() + "<br>Rating " + fr.getRating()
					+ "/5 </span></html>";

			reviewModel.addElement(review);
			reviewList.setModel(reviewModel);
		} else {
			System.err.println("Notification not a film review.");
		}
	}

	// Subscribe to a source which is referenced by a URL provided
	public void subscribeSource(String url) {
		SourceInterface source = NotificationSource.getNotSource(url);
		try {
			sink = new NotificationSink(this);

			source.registerSink(sink);
			urlSourceHash.put(url, source);

			sourceModel.addElement(url);
			sourceList.setModel(sourceModel);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Unsubscribes the sink from the selected source
	public void unsubSink(String url) {
		SourceInterface source = NotificationSource.getNotSource(url);
		try {
			sourceModel.removeElement(url);
			sourceList.setModel(sourceModel);

			source.unregisterSink(sink);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Used to add the components to the layout
	protected void addComponents() {
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);

		this.add(northPanel(), c);

		c.gridy = 1;

		this.add(southPanel(), c);

		JButton unsub = new JButton("Unsubscribe From Source");

		c.insets = new Insets(10, 10, 10, 10);
		c.gridy = 2;

		unsub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sourceList.isSelectionEmpty()) {
					String urlToRemove = (String) sourceList.getSelectedValue();
					unsubSink(urlToRemove);
				}
			}
		});

		this.add(unsub, c);
	}

}