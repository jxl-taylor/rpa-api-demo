package com.mr.api.mock.demo.lic;

import lombok.Data;

/**
 * Created by feng on 2020/3/30 0030
 */
@Data
public class LicContent {
	private String subject;
	//license content
	private String issuedTime = "";
	private String notBefore = "";
	private String notAfter = "";
	private String consumerType = "user";
	private int consumerAmount = 1;
	private String info = "this is a license";
}
