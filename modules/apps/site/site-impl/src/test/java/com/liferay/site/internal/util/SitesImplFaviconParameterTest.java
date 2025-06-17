/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.util;

import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Your Name
 */
public class SitesImplFaviconParameterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_sitesImpl = new SitesImpl();
	}

	@Test
	public void testGetLayoutSetPrototypesParametersWithFaviconPropagationDisabled()
		throws Exception {

		PropsTestUtil.setProps(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "false");

		Map<String, String[]> parameterMap = ReflectionTestUtil.invoke(
			_sitesImpl, "getLayoutSetPrototypesParameters",
			new Class<?>[] {boolean.class}, true);

		Assert.assertNotNull(parameterMap);

		String[] faviconValues = parameterMap.get(
			PortletDataHandlerKeys.FAVICON);

		Assert.assertNotNull(
			"FAVICON parameter should be present in the parameter map",
			faviconValues);
		Assert.assertEquals(
			"FAVICON parameter should have one value", 1, faviconValues.length);
		Assert.assertEquals(
			"FAVICON parameter should be false when propagation is disabled",
			Boolean.FALSE.toString(), faviconValues[0]);
	}

	@Test
	public void testGetLayoutSetPrototypesParametersWithFaviconPropagationEnabled()
		throws Exception {

		PropsTestUtil.setProps(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "true");

		Map<String, String[]> parameterMap = ReflectionTestUtil.invoke(
			_sitesImpl, "getLayoutSetPrototypesParameters",
			new Class<?>[] {boolean.class}, true);

		Assert.assertNotNull(parameterMap);

		String[] faviconValues = parameterMap.get(
			PortletDataHandlerKeys.FAVICON);

		Assert.assertNotNull(
			"FAVICON parameter should be present in the parameter map",
			faviconValues);
		Assert.assertEquals(
			"FAVICON parameter should have one value", 1, faviconValues.length);
		Assert.assertEquals(
			"FAVICON parameter should be true when propagation is enabled",
			Boolean.TRUE.toString(), faviconValues[0]);
	}

	@Test
	public void testGetLayoutSetPrototypesParametersLogoAndFaviconIndependent()
		throws Exception {

		// Set logo propagation to true and favicon propagation to false
		PropsTestUtil.setProps(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_LOGO, "true");
		PropsTestUtil.setProps(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "false");

		Map<String, String[]> parameterMap = ReflectionTestUtil.invoke(
			_sitesImpl, "getLayoutSetPrototypesParameters",
			new Class<?>[] {boolean.class}, true);

		String[] logoValues = parameterMap.get(PortletDataHandlerKeys.LOGO);
		String[] faviconValues = parameterMap.get(
			PortletDataHandlerKeys.FAVICON);

		Assert.assertEquals(
			"LOGO parameter should be true", Boolean.TRUE.toString(),
			logoValues[0]);
		Assert.assertEquals(
			"FAVICON parameter should be false", Boolean.FALSE.toString(),
			faviconValues[0]);

		// Set logo propagation to false and favicon propagation to true
		PropsTestUtil.setProps(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_LOGO, "false");
		PropsTestUtil.setProps(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "true");

		parameterMap = ReflectionTestUtil.invoke(
			_sitesImpl, "getLayoutSetPrototypesParameters",
			new Class<?>[] {boolean.class}, true);

		logoValues = parameterMap.get(PortletDataHandlerKeys.LOGO);
		faviconValues = parameterMap.get(PortletDataHandlerKeys.FAVICON);

		Assert.assertEquals(
			"LOGO parameter should be false", Boolean.FALSE.toString(),
			logoValues[0]);
		Assert.assertEquals(
			"FAVICON parameter should be true", Boolean.TRUE.toString(),
			faviconValues[0]);
	}

	private SitesImpl _sitesImpl;

}