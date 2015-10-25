package tweetmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


public class TestDBManager {
	public static void main(String[] args) throws Exception {
		
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider("Michelle"));
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		long startDateMilli = (new Date()).getTime() - (1L*1L*5L*60L*1000L); // 5mins ago.
        long endDateMilli = (new Date()).getTime();    // Now.
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String startDate = dateFormatter.format(startDateMilli);
        String endDate = dateFormatter.format(endDateMilli);
        System.out.println(startDate);
        System.out.println(endDate);
        
        String[] textList = { "Hi, SpOrt", "programming assignment.", "listeN"};
        
        //DynamoManager.FindTwitterWithKeyword(mapper, "miao");
        DynamoManager.FindTweetsPostedWithinTimePeriod(mapper, startDate, endDate);
        
        for(String text: textList){
        	System.out.format("%s\n", text);
        	System.out.println(DynamoManager.getCategory(text));
        }
        
	}
}
