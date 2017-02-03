package com.swarm.hystrix;

import java.io.StringReader;
import java.net.URI;

import javax.json.Json;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.swarm.rest.Employee;

/**
 * 
 * @author bhawani.singh
 *
 */

public class HelloServiceCommand extends HystrixCommand<Employee> {

	private final String name;
	long startTime = 0;

	private WebTarget helloService;
	String status;

	public HelloServiceCommand(String name) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SystemX"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("HelloServiceCommand"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloServiceCommand"))
				.andCommandPropertiesDefaults(
						// we default to a 500ms timeout for primary
						HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(1500)));

		this.name = name;
	}

	@Override
	protected Employee run() throws Exception {
		System.out.println("HelloServiceCommand Invoked");

		startTime = System.currentTimeMillis();

		System.out.println("\n\n@@@@@@@@@@@@ run()  Before callOtherService currentTimeMillis = " + startTime);

		callOtherService();

		System.out.println(
				"\n\n@@@@@@@@@@@@ run()  After callOtherService currentTimeMillis = " + (System.currentTimeMillis() - startTime));

		System.out.println("\n\n@@@@@@@@@@@@ run()  return Karnveer Singh Shekhawat");
		
		return new Employee("Karnveer Singh Shekhawat", "Sector 59", "Noida", "UP", "201301", "India");
	}

	//@Override
	protected Employee getFallback() {
		System.out.println("About to fallback");
		System.out.println("\n\n@@@@@@@@@@@@ getFallback()  After callOtherService currentTimeMillis = "
				+ (System.currentTimeMillis() - startTime));
		System.out.println("\n\n@@@@@@@@@@@@ getFallback()  return Bhawani Singh Shekhawat");
		return new Employee("Bhawani Singh Shekhawat", "Sector 59", "Noida", "UP", "201301", "India");
	}

	public void callOtherService() {
		Response response = getHelloService().request().get();

		Response.StatusType statusInfo = response.getStatusInfo();
		
		System.out.println("HelloServiceCommand.callOtherService() statusInfo.getFamily() = " + statusInfo.getFamily());
		System.out.println("HelloServiceCommand.callOtherService() statusInfo.getStatusCode() = " + statusInfo.getStatusCode());

		if (statusInfo.getFamily() == Response.Status.Family.SUCCESSFUL) {
			String responseVal= "";
			try{
				responseVal = response.getEntity().toString();
			} catch (Exception e){
				e.printStackTrace();
			}
			
			System.out.println("HelloServiceCommand.callOtherService() responseVal = " + responseVal);
		} else {
			System.out.println("HelloServiceCommand.callOtherService() call fail");
		}
	}

	public WebTarget getHelloService() {
		if (null == helloService) {
			helloService = ClientBuilder.newClient().target(UriBuilder
					.fromUri(URI.create("http://hello1-wildflyswarm.apps.10.2.2.2.xip.io")).path("/hello").build());
		}
		System.out.println("############   helloService.getUri = " + helloService.getUri().toString());
		return helloService;
	}

}
