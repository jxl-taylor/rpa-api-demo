package com.mr.api.mock.demo;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.api.mock.demo.lic.LicContent;
import com.mr.api.mock.demo.lic.MyLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 2020/3/30 0030
 */
@RestController
public class DemoController {

	@Autowired
	protected HttpServletRequest request;

//	public static String DOMAIN_URL = "http://localhost:8080";
	public static String DOMAIN_URL = "https://api.rpalinker.com";
	/**
	 * 流程：
	 * 客户端用户首次登录，控制中心发现没有心跳记录，则返回操作要求，即 operation=6
	 * 1、客户端收到operation = 6的请求后，随即发起请求下载demo的license，这里的demo license的生成时间从第一次心跳的时间计算
	 * 2、 当客户端付费后，license的天数 =  当前的剩余天数 + 付费购买的天数
	 *
	 * @return
	 */
	@GetMapping("/download/license")
	public ResponseEntity<byte[]> download() {
		LicContent licContent = new LicContent();
		//
		licContent.setIssuedTime("2020-03-28");//lic发布日期，当前日期
		licContent.setNotBefore("2020-03-27");//lic开始日期，一般是当前日期前一天
		/**
		 * lic到期日，这个日期可以通过mac地址从控制台上查出来。
		 * 如果mac不存在，表明第一次登录，给个默认的30天，也就是issueTime 加30天了
		 *
		 */
		licContent.setNotAfter("2022-04-09");
		//mac作为每个客户端的标识
		licContent.setSubject(request.getHeader("mac"));
		try {
			//生成lic 文件
			createLic(licContent);
			//打包publicKey与lic文件
			pack(licContent.getSubject());
			//download
			byte[] b = FileUtil.readBytes(MyLicense.LIC_DIR + File.separator + licContent.getSubject() + ".zip");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData(licContent.getSubject(), licContent.getSubject() + ".zip");
			return new ResponseEntity<byte[]>(b, headers, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtil.del(MyLicense.LIC_DIR + File.separator + licContent.getSubject() + ".zip");
			FileUtil.del(MyLicense.LIC_DIR + File.separator + licContent.getSubject());
		}
		throw new RuntimeException("download fail");
	}

	@GetMapping("/download/{botName}")
	public ResponseEntity<byte[]> downloadBot(@PathVariable String botName) {
		//download
		byte[] b = FileUtil.readBytes(System.getProperty("user.dir")
				+ File.separator + "bot"
				+ File.separator + botName + ".zip");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData(botName, botName + ".zip");
		return new ResponseEntity<byte[]>(b, headers, HttpStatus.OK);
	}

	@PostMapping("")
	public Map<String, Object> heartbeat(@RequestBody String body) {
		Map<String, Object> map = Maps.newHashMap();
		JSONObject jsonObject = JSON.parseObject(body);
		String serviceId = request.getHeader("serviceId");
		if (serviceId.equals("HEARTBEAT")) {
			map.put("resultcode", "1");
//			map.put("operation", "LIC_DOWNLOAD");
//			map.put("licDownloadUrl", "http://localhost:8080/download/license");
		} else if (serviceId.equals("REGISTER")) {
			map.put("resultcode", "1");
		} else if (serviceId.equals("QUERY_LIC_DOWNLOAD_URL")) {
			map.put("resultcode", "1");
			map.put("licDownloadUrl", DOMAIN_URL + "/download/license");
		} else if (serviceId.equals("REGISTER_QUERY")) { //根据mac地址或者序列号来查询用户的注册信息
			if (StringUtils.isEmpty(jsonObject.getString("serialNo"))) {
				map.put("resultcode", "0");
				map.put("message", "无序列号");
			} else {
				map.put("resultcode", "1");
				map.put("userType", "ENT");
				map.put("companyName", "迈容");
				map.put("companyAddress", "上海");
				map.put("applicant", "冯江");
				map.put("applyPhone1", "19999999999");
				map.put("applyPhone2", "");
				map.put("applyMail", "jfeng@microrule.com");
			}
		} else if (serviceId.equals("BOT_MARKET_QUERY")) {
			map.put("resultcode", "1");
			Map<String, Map<String, String>> resultMap = Maps.newLinkedHashMap();
			Map<String, String> map1 = Maps.newLinkedHashMap();
			map1.put("botName", "demo1");
			map1.put("mainBot", "demo1.kjb");
			map1.put("desp", "第一个BOT");
			map1.put("version", "v1.2");
			map1.put("downloadUrl", DOMAIN_URL + "/download/demo1");
			map1.put("createdBy", "jiang.feng");
			map1.put("createdTime", "2020-04-08");
			Map<String, String> map2 = Maps.newLinkedHashMap();
			map2.put("botName", "demo2");
			map2.put("mainBot", "demo2.kjb");
			map2.put("desp", "第二个BOT");
			map2.put("version", "v2");
			map2.put("downloadUrl", DOMAIN_URL + "/download/demo2");
			map2.put("createdBy", "jiang.feng");
			map2.put("createdTime", "2020-04-09");
			Map<String, String> map3 = Maps.newLinkedHashMap();
			map3.put("botName", "demo3");
			map3.put("mainBot", "demo3.kjb");
			map3.put("desp", "第三个BOT");
			map3.put("version", "v2");
			map3.put("downloadUrl", DOMAIN_URL + "/download/demo3");
			map3.put("createdBy", "jiang.feng");
			map3.put("createdTime", "2020-04-09");
			Map<String, String> map4 = Maps.newLinkedHashMap();
			map4.put("botName", "demo4");
			map4.put("mainBot", "demo4.kjb");
			map4.put("desp", "第四个BOT");
			map4.put("version", "v2");
			map4.put("downloadUrl", DOMAIN_URL + "/download/demo4");
			map4.put("createdBy", "jiang.feng");
			map4.put("createdTime", "2020-04-09");

			resultMap.put("demo1", map1);
			resultMap.put("demo2", map2);
			resultMap.put("demo3", map3);
			resultMap.put("demo4", map4);

			List<Map<String, String>> list = Lists.newArrayList();
			if (!StringUtils.isEmpty(jsonObject.getString("botName"))) {
				Map<String, String> sMap = resultMap.get(jsonObject.getString("botName"));
				if (sMap != null) list.add(sMap);
			} else {
				list.addAll(resultMap.values());
			}
			map.put("botContent", list);
		}

		return map;
	}

	private void pack(String subject) {
		MyLicense.pack(subject);
	}

	private void createLic(LicContent licContent) throws Exception {
		MyLicense.initParam();
		//生成证书
		MyLicense.create(licContent);
	}

}
