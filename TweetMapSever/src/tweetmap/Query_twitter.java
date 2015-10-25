package tweetmap;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.Iterator;

import org.json.JSONObject;

public class Query_twitter {
	public static void main(String[] args) throws Exception {
		ItemCollection<QueryOutcome> items;
		items = DocumentAPIQuery.QueryDocument();
		
		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }
		
	}
}
