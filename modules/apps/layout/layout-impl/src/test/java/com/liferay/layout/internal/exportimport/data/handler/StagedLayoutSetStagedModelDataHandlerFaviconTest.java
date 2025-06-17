/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.layout.internal.exportimport.staged.model.repository.StagedLayoutSet;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.w3c.dom.Element;

/**
 * @author Your Name
 */
public class StagedLayoutSetStagedModelDataHandlerFaviconTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		_stagedLayoutSetStagedModelDataHandler =
			new StagedLayoutSetStagedModelDataHandler();
	}

	@Test
	public void testExportFaviconFileEntrySkippedWhenParameterIsFalse()
		throws Exception {

		// Setup parameter map with FAVICON set to false
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put(
			PortletDataHandlerKeys.FAVICON,
			new String[] {Boolean.FALSE.toString()});

		Mockito.when(
			_portletDataContext.getParameterMap()
		).thenReturn(
			parameterMap
		);

		// Setup mock layout set with favicon
		Mockito.when(
			_layoutSet.getFaviconFileEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_stagedLayoutSet.getLayoutSet()
		).thenReturn(
			_layoutSet
		);

		// Call the private method
		ReflectionTestUtil.invoke(
			_stagedLayoutSetStagedModelDataHandler, "_exportFaviconFileEntry",
			new Class<?>[] {
				PortletDataContext.class, StagedLayoutSet.class, Element.class
			},
			_portletDataContext, _stagedLayoutSet, _element);

		// Verify that no interactions occurred with the LayoutSet
		// (method should return early due to favicon parameter being false)
		Mockito.verify(_layoutSet, Mockito.never()).getFaviconFileEntryId();
	}

	@Test
	public void testImportFaviconFileEntrySkippedWhenParameterIsFalse()
		throws Exception {

		// Setup parameter map with FAVICON set to false
		Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put(
			PortletDataHandlerKeys.FAVICON,
			new String[] {Boolean.FALSE.toString()});

		Mockito.when(
			_portletDataContext.getParameterMap()
		).thenReturn(
			parameterMap
		);

		// Call the private method
		ReflectionTestUtil.invoke(
			_stagedLayoutSetStagedModelDataHandler, "_importFaviconFileEntry",
			new Class<?>[] {
				PortletDataContext.class, StagedLayoutSet.class, Element.class
			},
			_portletDataContext, _stagedLayoutSet, _element);

		// Verify that no interactions occurred with the StagedLayoutSet
		// (method should return early due to favicon parameter being false)
		Mockito.verify(_stagedLayoutSet, Mockito.never()).getLayoutSet();
	}

	@Mock
	private Element _element;

	@Mock
	private LayoutSet _layoutSet;

	@Mock
	private PortletDataContext _portletDataContext;

	@Mock
	private StagedLayoutSet _stagedLayoutSet;

	private StagedLayoutSetStagedModelDataHandler
		_stagedLayoutSetStagedModelDataHandler;

}