import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.BindException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Server for the film review application
 * @author Jonathan Harper
 */
public class FilmReviewServer {

	public static void main(String[] args) {
		Registry r;
		try {
			r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			ServerGUI frame = new ServerGUI("Create A Film Review", r);
			frame.init();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			
		}
	}
}

class ServerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private Registry r;
	private String url = "";
	private SourceInterface source;

	protected ServerGUI(String title, Registry r) {
		super(title);
		this.r = r;
	}

	protected void init() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		addComponents();

		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	// Used to add the components to the layout
	protected void addComponents() {
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 0, 10);

		this.add(new JLabel("Author", SwingConstants.CENTER), c);

		c.gridx = 1;
		c.insets = new Insets(10, 10, 0, 10);

		JTextField author = new JTextField(20);

		this.add(author, c);

		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;

		JButton makeUrl = new JButton("Make URL");

		// Produces a URL and connected source
		makeUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (author.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(author, "Please provide an author.");
				} else {
					try {
						url = "rmi://" + author.getText().trim() + "/"
								+ InetAddress.getLocalHost().getHostName().trim();
						source = new NotificationSource(url);
						r.rebind(url, source);

						JOptionPane.showMessageDialog(makeUrl, "Your URL is: " + url);
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (AccessException e1) {
						e1.printStackTrace();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		this.add(makeUrl, c);

		c.gridy = 2;
		c.gridwidth = 1;

		this.add(new JLabel("Film Title", SwingConstants.CENTER), c);

		JTextField filmTitle = new JTextField(20);
		c.gridx = 1;
		c.insets = new Insets(10, 10, 0, 10);

		this.add(filmTitle, c);

		c.gridy = 3;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;

		this.add(new JLabel("Review", SwingConstants.CENTER), c);

		JTextArea filmReview = new JTextArea(20, 20);
		filmReview.setLineWrap(true);
		filmReview.setWrapStyleWord(true);
		filmReview.setBorder(filmTitle.getBorder());

		c.gridx = 1;
		c.insets = new Insets(10, 10, 10, 10);

		this.add(filmReview, c);

		JSlider rating = new JSlider(0, 5);
		rating.setMajorTickSpacing(1);
		rating.setPaintTicks(true);
		rating.setPaintLabels(true);
		rating.setSnapToTicks(true);

		c.gridy = 4;
		c.gridx = 0;
		c.insets = new Insets(10, 10, 10, 10);

		this.add(new JLabel("Rating", SwingConstants.CENTER), c);

		c.gridx = 1;
		c.insets = new Insets(10, 10, 10, 10);

		this.add(rating, c);

		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridwidth = 2;

		JButton submit = new JButton("Submit");

		// Publishes the film review to the sources
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (filmReview.getText().trim().isEmpty() || filmTitle.getText().trim().isEmpty()
							|| author.getText().trim().isEmpty()) {
						JOptionPane.showMessageDialog(filmReview, "Please fill in all of the fields before submitting");
					} else {
						System.out.println(url);
						if (url.trim().isEmpty()) {
							JOptionPane.showMessageDialog(filmReview, "Please produce a URL before proceeding");
						} else {
							try {
								FilmReview review = new FilmReview(filmTitle.getText(), author.getText(),
										filmReview.getText(), url, rating.getValue());
								;

								source.notifySinks(review);

								System.out.println("Sinks have been notified.");
							} catch (RemoteException e1) {
								e1.printStackTrace();
							}
						}
					}

				} catch (HeadlessException e) {
					e.printStackTrace();
				}
			}

		});

		this.add(submit, c);
	}

}