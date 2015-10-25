package tweetmap;

import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.HashMap;
import java.util.Iterator;
//import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
//import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

public class DocumentAPIQuery {

    static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new ProfileCredentialsProvider("Michelle")));

    static String tableName = "twitter";

    public static ItemCollection<QueryOutcome> QueryDocument(){

        Long tp =1800L;   // mins
        ItemCollection<QueryOutcome> item = findTweetsInLastWithConfig(tp);
        System.out.println("The data in item");
        return item;
    }
    
    private static ItemCollection<QueryOutcome> findTweetsInLastWithConfig(Long timePeriod) {

        Table table = dynamoDB.getTable(tableName);

        long timePeriodAgo = (new Date()).getTime() - (1L*1L*timePeriod*60L*1000L);

        Date periodAgo = new Date();
        periodAgo.setTime(timePeriodAgo);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String agoStr = df.format(periodAgo);

        RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("time").gt(agoStr); //after time agoStr
        QuerySpec spec = new QuerySpec()
            .withHashKey("fid", "1")
            .withHashKey("twid", "a" )
            .withRangeKeyCondition(rangeKeyCondition);
        ItemCollection<QueryOutcome> items = table.query(spec);

        System.out.println("\nfind Tweets after "+ agoStr + " results:");
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }
        return items;
    }
}