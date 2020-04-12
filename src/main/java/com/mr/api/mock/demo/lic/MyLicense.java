package com.mr.api.mock.demo.lic;

/**
 * Created by feng on 2020/2/28
 */

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import de.schlichtherle.license.*;
import lombok.extern.log4j.Log4j;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * CreateLicense
 *
 * @author melina
 */
@Log4j
public class MyLicense {

	public static String LIC_DIR = System.getProperty("user.dir") + File.separator + "lic";
	private static String LIC_SAVE_DIR = LIC_DIR + File.separator + "%s" + File.separator;
	//common param
	private static String PRIVATEALIAS = "";
	private static String KEYPWD = "";
	private static String STOREPWD = "";
	private static String licPath = "";
	private static String priPath = "";
	private static String pubPath = "";

	// 为了方便直接用的API里的例子
	// X500Princal是一个证书文件的固有格式，详见API
	private final static X500Principal DEFAULTHOLDERANDISSUER = new X500Principal(
			"CN=Duke、OU=JavaSoft、O=Sun Microsystems、C=US");

	public static void initParam() throws Exception {
		initParam("createParam.properties");
	}

	public static void initParam(String propertiesPath) throws Exception {
		// 获取参数
		Properties prop = new Properties();
		InputStream in = MyLicense.class.getClassLoader().getResourceAsStream(propertiesPath);
		prop.load(in);
		PRIVATEALIAS = prop.getProperty("PRIVATEALIAS");
		KEYPWD = prop.getProperty("KEYPWD");
		STOREPWD = prop.getProperty("STOREPWD");
		KEYPWD = prop.getProperty("KEYPWD");
		licPath = prop.getProperty("licPath");
		priPath = prop.getProperty("priPath");
		pubPath = prop.getProperty("pubPath");

	}

	/**
	 * 证书发布者端执行,生成license
	 *
	 * @param licContent
	 * @return
	 */
	public static boolean create(LicContent licContent) {
		try {
			FileUtil.mkdir(String.format(LIC_SAVE_DIR, licContent.getSubject()));
			LicenseManager licenseManager = LicenseManagerHolder
					.getLicenseManager(initLicenseParams0(licContent.getSubject()));
			licenseManager.store((createLicenseContent(licContent)),
					FileUtil.newFile(String.format(LIC_SAVE_DIR + licPath, licContent.getSubject())));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端证书生成失败!");
			return false;
		}
		System.out.println("服务器端生成证书成功!");
		return true;
	}

	/**
	 * 打包publiceKey.store 与 lic文件
	 *
	 * @param subject
	 */
	public static void pack(String subject) {
		ZipUtil.zip(FileUtil.file(LIC_DIR + File.separator + subject + ".zip"),
				false,
				FileUtil.file(String.format(LIC_SAVE_DIR + licPath, subject)),
				FileUtil.file(System.getProperty("user.dir") + File.separator + pubPath));
	}

	// 返回生成证书时需要的参数
	private static LicenseParam initLicenseParams0(String subject) {
		Preferences preference = Preferences
				.userNodeForPackage(MyLicense.class);
		// 设置对证书内容加密的对称密码
		CipherParam cipherParam = new DefaultCipherParam(STOREPWD);
		// 参数1,2从哪个Class.getResource()获得密钥库;参数3密钥库的别名;参数4密钥库存储密码;参数5密钥库密码
		KeyStoreParam privateStoreParam = new MyKeyStoreParam(
				MyLicense.class, priPath, PRIVATEALIAS, STOREPWD, KEYPWD);
		return new DefaultLicenseParam(subject,
				preference, privateStoreParam, cipherParam);
	}

	// 从外部表单拿到证书的内容
	private static LicenseContent createLicenseContent(LicContent licContent) throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		LicenseContent content = new LicenseContent();
		content.setSubject(licContent.getSubject());
		content.setHolder(DEFAULTHOLDERANDISSUER);
		content.setIssuer(DEFAULTHOLDERANDISSUER);
		content.setIssued(format.parse(licContent.getIssuedTime()));
		content.setNotBefore(format.parse(licContent.getNotBefore()));
		content.setNotAfter(format.parse(licContent.getNotAfter()));
		content.setConsumerType(licContent.getConsumerType());
		content.setConsumerAmount(licContent.getConsumerAmount());
		content.setInfo(licContent.getInfo());
		// 扩展
		content.setExtra(new Object());
		return content;
	}
}
