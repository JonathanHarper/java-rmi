

/**
 * Notification object for the film review application
 * @author Jonathan Harper
 */
public class FilmReview extends Notification {

	private static final long serialVersionUID = 1L;
	
	private final String criticName, review, url, filmTitle;
	//Rating /5, 0 being the worst
	private final int rating;
	
	public FilmReview(String filmTitle, String criticName, String review, String url, int rating) {
		this.filmTitle = filmTitle;
		this.criticName = criticName;
		this.review = review;
		this.url = url;
		this.rating = rating;
	}

	public String getUrl() {
		return url;
	}
	
	public String getReview() {
		return review;
	}
	
	public String getSongname() {
		return review;
	}
	
	public String getCriticName() {
		return criticName;
	}

	public String getFilmTitle() {
		return filmTitle;
	}
	
	public int getRating() {
		return rating;
	}
	
}
