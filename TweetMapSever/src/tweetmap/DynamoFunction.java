package tweetmap;

import java.util.ArrayList;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

/*Create Dynamo Table for recoding Tweets*/
public class DynamoFunction {
	/*Create Instance for dynamoDB manipulation*/
	static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
			new ProfileCredentialsProvider("Michelle")));
	/*
	 * 
	 * Create Table for tableName
	 * 
	 * */
	static public void CreateTable(String tableName) throws Exception {
		try {
			deleteTable(tableName);
			createTable(tableName, 10L, 5L, "primary", "S", "time", "S");
		} catch (Exception e) {
			System.err.println("Program failed:");
			System.err.println(e.getMessage());
		}
		System.out.println("Successfully Create table:" + tableName + ".");
	}
	
	/*
	 * Delete Table : tableName from DynamoDB
	 * 
	 * */
	private static void deleteTable(String tableName) {
		Table table = dynamoDB.getTable(tableName);
		try {
			System.out.println("Issuing DeleteTable request for " + tableName);
			table.delete();
			System.out.println("Waiting for " + tableName
					+ " to be deleted...this may take a while...");
			table.waitForDelete();

		} catch (Exception e) {
			System.err.println("DeleteTable request failed for " + tableName);
			System.err.println(e.getMessage());
		}
	}
	
	/*
	 * Create Table in DynamoDB
	 * 
	 * */
	private static void createTable(String tableName, long readCapacityUnits,
			long writeCapacityUnits, String hashKeyName, String hashKeyType,
			String rangeKeyName, String rangeKeyType) {

		try {

			ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
			keySchema.add(new KeySchemaElement().withAttributeName(hashKeyName)
					.withKeyType(KeyType.HASH));

			ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
			attributeDefinitions.add(new AttributeDefinition()
					.withAttributeName(hashKeyName).withAttributeType(
							hashKeyType));

			if (rangeKeyName != null) {
				keySchema.add(new KeySchemaElement().withAttributeName(
						rangeKeyName).withKeyType(KeyType.RANGE));
				attributeDefinitions.add(new AttributeDefinition()
						.withAttributeName(rangeKeyName).withAttributeType(
								rangeKeyType));
			}
			//attributeDefinitions.add(new AttributeDefinition()
			//		.withAttributeName("twid").withAttributeType("S")); 
																		

			// define global secondary index
			GlobalSecondaryIndex secondIndex = new GlobalSecondaryIndex()
					.withIndexName("twid")
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(
									(long) 10).withWriteCapacityUnits((long) 1))
					.withProjection(
							new Projection()
									.withProjectionType(ProjectionType.ALL));

			ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();

			indexKeySchema.add(new KeySchemaElement().withAttributeName("twid")
					.withKeyType(KeyType.HASH));

			secondIndex.setKeySchema(indexKeySchema);
			CreateTableRequest request = new CreateTableRequest()
					.withTableName(tableName)
					.withKeySchema(keySchema)
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(
									readCapacityUnits).withWriteCapacityUnits(
									writeCapacityUnits));
					//.withGlobalSecondaryIndexes(secondIndex);
					
			request.setAttributeDefinitions(attributeDefinitions);

			System.out.println("Issuing CreateTable request for " + tableName);
			Table table = dynamoDB.createTable(request);
			System.out.println("Waiting for " + tableName
					+ " to be created...this may take a while...");
			table.waitForActive();

		} catch (Exception e) {
			System.err.println("CreateTable request failed for " + tableName);
			System.err.println(e.getMessage());
		}
	}
}
