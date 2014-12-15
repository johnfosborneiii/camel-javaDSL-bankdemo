package org.apache.camel.demos.bankdemo;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class bankTransactionJavaDSL {
	
	public static void main(String args[]) throws Exception {
		
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
				public void configure() {
					from("file://inputdir?noop=true")
			    		.split(xpath("//bank/transaction"))
			    		.setHeader("CustId", xpath("/transaction/CustId/text()", String.class))
			    		.setHeader("VipStatus", xpath("/transaction/VipStatus/text()", String.class))
			    		.setHeader("amt", xpath("/transaction/Detail/amount/text()", String.class))
			    		.setHeader("anotherHeader",simple("${body}"))
			    		.choice()
			    			.when(xpath("/transaction[@type='Transfer']"))
			    				.log("Making a transfer")
			    				.setHeader("amt", xpath("/transaction/Detail/amount/text()", String.class))
			    				.to("file://outpurdir?fileName=transfer-$simple{date:now:yyyyMMddhhmmss.mmm}.xml")
			    			.when(xpath("/transaction[@type='Cash']"))
			    				.log("Getting some cash")
			    				.to("file://outpurdir?fileName=cash-$simple{date:now:yyyyMMddhhmmss.mmm}.xml");
			    			
			    			//otherwise().to("");
			    		
				}
		});
			
		context.start();
		Thread.sleep(2000);
		context.stop();
	}
}
