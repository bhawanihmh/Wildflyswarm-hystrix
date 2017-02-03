package com.swarm.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.swarm.hystrix.HelloServiceCommand;
/**
 * 
 * @author bhawani.singh
 *
 */
@Path("swarmhystrixapp")
public class SwarmREST {

	@GET
	@Path("getemployee")
    @Produces({"application/xml", "application/json"})
    public Employee getEmployee() {
		
		System.out.println("In SwarmREST getEmployee()");
		Employee employee = null;
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
		try {
			ConfigurationManager.getConfigInstance().setProperty("primarySecondary.usePrimary", true);
			employee = new HelloServiceCommand("Swarm REST Service").execute();
			System.out.println("\n\n@@@@@@@@@@@@@@  SwarmREST.getEmployee()  employee name = " + employee.getName() + "\n\n");
		} finally {
			context.shutdown();
			ConfigurationManager.getConfigInstance().clear();
		}
		
        return employee;
    }
}
