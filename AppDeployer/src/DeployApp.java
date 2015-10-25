import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentTier;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;


public class DeployApp {

	private AmazonEC2 ec2;
	private AmazonS3 s3;
	private final String appName = "TweetMap";
	private final String envName = "tweetmap-env";
	private final String versionName = "tweetmap-v0";
	private final String ec2Endpoint = "ec2.us-east-1.amazonaws.com";
	private final String elasticEndpoint = "elasticbeanstalk.us-east-1.amazonaws.com";
	private final String bucketName = "elasticbeanstalk-us-east-1-654279747971";
	private final String stackSolName = "64bit Amazon Linux 2015.03 v2.0.1 running Tomcat 7 Java 7";
	
	/*private final String appName = "yuzheTestApp";
	private final String envName = "yuzheTestEnv";
	private final String ec2Endpoint = "ec2.us-west-2.compute.amazonaws.com";
	private final String elasticEndpoint = "elasticbeanstalk.us-west-2.amazonaws.com";
	private final String bucketName = "elasticbeanstalk-us-west-2-654279747971";*/
	
	private AWSElasticBeanstalk beanstalk;
	
	public DeployApp() {
		AWSCredentials credential = null;
		try {
			credential = new ProfileCredentialsProvider("YuzheShenCloud").getCredentials();
		} catch (Exception e) {
			System.out.println("Wrong Credential");
			return;
		}
		ec2 = new AmazonEC2Client(credential);
		ec2.setEndpoint(ec2Endpoint);
		
		s3 = new AmazonS3Client(credential);

		beanstalk = new AWSElasticBeanstalkClient(credential);
		beanstalk.setEndpoint(elasticEndpoint);
	}

	/*private void createApplication() {
		CreateApplicationRequest createApplicationRequest = new CreateApplicationRequest()
			.withApplicationName(appName)
			.withDescription("deploy tweet map");
		beanstalk.createApplication(createApplicationRequest);
	}*/

	private void setUpEnvironment() {
		Collection<ConfigurationOptionSetting> autoScaleSet = new ArrayList<ConfigurationOptionSetting>();
		autoScaleSet.add(new ConfigurationOptionSetting("aws:autoscaling:asg", "MaxSize", "1"));
		autoScaleSet.add(new ConfigurationOptionSetting("aws:elb:loadbalancer", "CrossZone", "true"));
		autoScaleSet.add(new ConfigurationOptionSetting("aws:autoscaling:launchconfiguration", "InstanceType", "t2.micro"));
		autoScaleSet.add(new ConfigurationOptionSetting("aws:elb:policies", "ConnectionDrainingEnabled", "true"));
		autoScaleSet.add(new ConfigurationOptionSetting("aws:elb:policies", "ConnectionDrainingTimeout", "20"));

		CreateEnvironmentRequest createEnvironmentRequest = new CreateEnvironmentRequest()
			.withTier(new EnvironmentTier().withName("WebServer").withType("Standard"))
			.withApplicationName(appName)
			.withEnvironmentName(envName)
			.withOptionSettings(autoScaleSet)
			.withVersionLabel(versionName)
			.withSolutionStackName(stackSolName);
								// "64bit Amazon Linux 2014.09 v1.2.0 running Tomcat 8 Java 8"
		beanstalk.createEnvironment(createEnvironmentRequest);
	}

	private void createS3Version(String filePath, boolean autoCreate) {
		String s3Key = appName + ".war";
		File uploadFile = new File(filePath);
		if (uploadFile.exists())
			s3.putObject(bucketName, s3Key, uploadFile);
		S3Location sourceBundle = new S3Location(bucketName, s3Key);
		CreateApplicationVersionRequest createApplicationVersionRequest = new CreateApplicationVersionRequest();
		createApplicationVersionRequest.withApplicationName(appName)
									   .withVersionLabel(versionName)
									   .withAutoCreateApplication(autoCreate)
									   .withSourceBundle(sourceBundle);
		beanstalk.createApplicationVersion(createApplicationVersionRequest);
	}
	
	private void updateEnvironment() {
		UpdateEnvironmentRequest updateEnvironmentRequest = new UpdateEnvironmentRequest();
		updateEnvironmentRequest
			.withEnvironmentName(envName)
			.withVersionLabel(versionName);
		beanstalk.updateEnvironment(updateEnvironmentRequest);
	}

	public void deployApp(String filePath) {
		createS3Version(filePath, false);
		updateEnvironment();
	}

	public void createEnvironment(String filePath) {
		createS3Version(filePath, true);
		setUpEnvironment();
	}
	
	public static void main(String[] args) {
		String warPath = "/Users/trunksexy/work/CU/2015fall/cloud_computing/TweetMap.war";
		DeployApp deployer = new DeployApp();
		deployer.createEnvironment(warPath);
		//deployer.deployApp(warPath);
	}
}
