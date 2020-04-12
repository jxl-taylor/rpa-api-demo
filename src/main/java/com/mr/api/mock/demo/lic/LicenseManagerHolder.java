package com.mr.api.mock.demo.lic;

import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * Created by feng on 2020/2/28
 */
public class LicenseManagerHolder {
	private static LicenseManager licenseManager;

	public static synchronized LicenseManager getLicenseManager(LicenseParam licenseParams) {
		if (licenseManager == null) {
			licenseManager = new LicenseManager(licenseParams);
		}
		return licenseManager;
	}
}
