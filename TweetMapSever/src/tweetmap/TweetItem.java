package tweetmap;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;

import twitter4j.Status;


public class TweetItem {
	public long twid;
	public double lon;
	public double lat;
	public List<String> keywords = new ArrayList();
	public String timeStamp;
	public String text;
	
	void tagKewords() {
		for (int i = 0; i < Keywords.keywords.length; i++)
			if (text.toLowerCase().contains(Keywords.keywords[i]))
				keywords.add(Keywords.keywords[i]);
	}
	
	private void parseAndSaveTweet(Status status) {
		this.twid = status.getId();
		this.lon = status.getGeoLocation().getLongitude();
		this.lat = status.getGeoLocation().getLatitude();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.timeStamp = dateFormatter.format(status.getCreatedAt());
		this.text = status.getText();
		tagKewords();
	}
	
	public TweetItem(Status status) {
		// TODO Auto-generated constructor stub
		parseAndSaveTweet(status);
	}
	
	public TweetItem(Item item) {
		
	}
}
