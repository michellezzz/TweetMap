package tweetmap;

import java.io.IOException;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;


public class DynamoManager {

	static final String tableName = "twitter";
	
	static public DynamoDB dynamoDB = new DynamoDB ((AmazonDynamoDB) new 
       		AmazonDynamoDBClient(getCredential()));
	
	public static AWSCredentials getCredential() {
		try {
			return new PropertiesCredentials(DynamoManager.class.getResourceAsStream("/AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Table table = dynamoDB.getTable(tableName);
	
	public static DynamoDBMapper getSampleMapper() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(getCredential());
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		return mapper;
	}
	
    public static List<Twitter> FindTwitterWithKeyword(
            DynamoDBMapper mapper,
            String value) throws Exception {
          
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition("keyword", 
                new Condition()
                   .withComparisonOperator(ComparisonOperator.EQ)
                   .withAttributeValueList(new AttributeValue().withS(value)));
        
        List<Twitter> scanResult = mapper.scan(Twitter.class, scanExpression);
  
        
        return scanResult;
    }
    
    public static List<Twitter> FindTweetsPostedWithinTimePeriod(
            DynamoDBMapper mapper,
            String startDate,
            String endDate) throws Exception {
    
        Condition rangeKeyCondition = new Condition()
            .withComparisonOperator(ComparisonOperator.BETWEEN.toString())
            .withAttributeValueList(new AttributeValue().withS(startDate), 
                                    new AttributeValue().withS(endDate));
        
        Twitter twitter_key = new Twitter();
        twitter_key.setPrimary("tweet");
        
        DynamoDBQueryExpression<Twitter> queryExpression = new DynamoDBQueryExpression<Twitter>()
            .withHashKeyValues(twitter_key)
            .withRangeKeyCondition("time", rangeKeyCondition);
                
        List<Twitter> betweenTwitter = mapper.query(Twitter.class, queryExpression);
        
        return betweenTwitter;
    }
	
	public void inertTweet(TweetItem tweet) {
		Item dynamoItem = new Item();
		if (tweet.keywords.isEmpty()) { // ??
			String keyword = getCategory(tweet.text);
			dynamoItem
			.withPrimaryKey("primary", "tweet")
			.withString("time", tweet.timeStamp)
			.withString("twid", tweet.twid + "")
			.withString("keyword", keyword)
			.withDouble("lon", tweet.lon)
			.withDouble("lat", tweet.lat)
			.withString("text", tweet.text);
		}
		table.putItem(dynamoItem);
	}
	
	public static String getCategory(String text){  // TODO ! 
		//String alchemyKey = "94ec1cc9153ddcbcc8d5f980ec31932dbec4d9fb";
		String[] sports = {"sports", "sport", "basketball", "footbal", "soccer", "baseball", "volleyball", "kobe"};
		String[] entertainment = {"music", "r&b", "blues", "album","listen","video", "mv", "taylor", 
				"big bang", "aveng", "play", "nintendo","oscar"};
		String[] technology = {"computer", "prog", "hack", "silicon", "aws", "network", 
				"database", "internet", "data", "anal", "sci","iPhone","iPad","Samsung","geek","tech"};
		
		String keyword = "none";
		
		text = text.toLowerCase();
		System.out.println(text);
		for(String cat:sports){
			if(text.indexOf(cat)!=-1) {
				keyword = "sports";
				return keyword;
			}
		}
		
		for(String cat:entertainment){
			if(text.indexOf(cat)!=-1) {
				keyword = "entertainment";
				return keyword;
			}		
		}
		
		for(String cat:technology){
			if(text.indexOf(cat)!=-1) {
				keyword = "technology";
				return keyword;
			}
		}
		
		if (keyword=="") keyword = "none";
		return keyword;
	}
	
	@DynamoDBTable(tableName="twitter")
    public static class Twitter {
		private String primary;
		private String twid;
		private String keyword;
		private String time;
		private double lon;
		private double lat;
		private String text;

		@DynamoDBHashKey(attributeName="primary")  // hash key
        public String getPrimary() { return primary; }
        public void setPrimary(String primary) { this.primary = primary; }
        
        @DynamoDBRangeKey(attributeName="time")  // range key
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        
        @DynamoDBAttribute(attributeName="twid")
        public String getTwid() { return twid; }
        public void setTwid(String twid) { this.twid = twid; }
        
        @DynamoDBAttribute(attributeName="keyword")
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
 
        @DynamoDBAttribute(attributeName="lon")
        public double getLon() { return lon; }    
        public void setLon(double lon) { this.lon = lon; }
        
        @DynamoDBAttribute(attributeName="lat")
        public double getLat() { return lat; }    
        public void setLat(double lat) { this.lat = lat; }
        
        @DynamoDBAttribute(attributeName="text")
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
       
        @Override
        public String toString() {
            return "Twitter [twid=" + twid + ", keyword=" + keyword + ", time=" + time
            + ", lon=" + lon + ", lat=" + lat
            + ", text=" + text + "]";            
        }
    }
	   
}
