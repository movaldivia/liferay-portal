/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Your Name
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypeFaviconPropagationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		// Create a layout set prototype
		_layoutSetPrototype = LayoutSetPrototypeLocalServiceUtil.addLayoutSetPrototype(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_createLayoutSetPrototypeNameMap(), null, true, true,
			ServiceContextTestUtil.getServiceContext());

		_layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		// Create a site from the template
		ServiceContext serviceContext = ServiceContextTestUtil.getServiceContext();
		serviceContext.setAttribute(
			"layoutSetPrototypeLinkEnabled", Boolean.TRUE);
		serviceContext.setAttribute(
			"layoutSetPrototypeUuid", _layoutSetPrototype.getUuid());

		_group = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, serviceContext);

		// Enable merging
		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);
	}

	@Test
	public void testFaviconNotPropagatedWhenPropertyIsFalse() throws Exception {
		// Set the property to false (default behavior)
		TestPropsUtil.set(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "false");

		// Add favicon to the layout set prototype
		FileEntry prototypeFaviconFileEntry = _addFileEntry(_getFaviconBytes());
		
		LayoutSet prototypeLayoutSet = _layoutSetPrototype.getLayoutSet();
		prototypeLayoutSet.setFaviconFileEntryId(
			prototypeFaviconFileEntry.getFileEntryId());
		
		_layoutSetLocalService.updateLayoutSet(prototypeLayoutSet);

		// Add a different favicon to the site
		FileEntry siteFaviconFileEntry = _addFileEntry(_getDifferentFaviconBytes());
		
		LayoutSet siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);
		
		siteLayoutSet.setFaviconFileEntryId(
			siteFaviconFileEntry.getFileEntryId());
		
		_layoutSetLocalService.updateLayoutSet(siteLayoutSet);

		// Propagate changes from template to site
		_propagateChanges(_group);

		// Verify that the site's favicon was NOT changed
		siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);
		
		Assert.assertEquals(
			"Site favicon should not have been changed by template propagation",
			siteFaviconFileEntry.getFileEntryId(),
			siteLayoutSet.getFaviconFileEntryId());
	}

	@Test
	public void testFaviconPropagatedWhenPropertyIsTrue() throws Exception {
		// Set the property to true
		TestPropsUtil.set(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "true");

		// Add favicon to the layout set prototype
		FileEntry prototypeFaviconFileEntry = _addFileEntry(_getFaviconBytes());
		
		LayoutSet prototypeLayoutSet = _layoutSetPrototype.getLayoutSet();
		prototypeLayoutSet.setFaviconFileEntryId(
			prototypeFaviconFileEntry.getFileEntryId());
		
		_layoutSetLocalService.updateLayoutSet(prototypeLayoutSet);

		// Add a different favicon to the site
		FileEntry siteFaviconFileEntry = _addFileEntry(_getDifferentFaviconBytes());
		
		LayoutSet siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);
		
		siteLayoutSet.setFaviconFileEntryId(
			siteFaviconFileEntry.getFileEntryId());
		
		_layoutSetLocalService.updateLayoutSet(siteLayoutSet);

		// Propagate changes from template to site
		_propagateChanges(_group);

		// Verify that the site's favicon WAS changed to match the template
		siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);
		
		Assert.assertNotEquals(
			"Site favicon should have been changed by template propagation",
			siteFaviconFileEntry.getFileEntryId(),
			siteLayoutSet.getFaviconFileEntryId());
	}

	@Test
	public void testFaviconInitiallyCopiedAndStopsAfterManualChange() 
		throws Exception {
		
		// Enable favicon propagation for this test
		TestPropsUtil.set(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "true");

		// Create a new layout set prototype with favicon
		LayoutSetPrototype newLayoutSetPrototype = 
			LayoutSetPrototypeLocalServiceUtil.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				_createLayoutSetPrototypeNameMap(), null, true, true,
				ServiceContextTestUtil.getServiceContext());

		// Add favicon to the new prototype
		FileEntry faviconFileEntry = _addFileEntry(_getFaviconBytes());
		
		LayoutSet prototypeLayoutSet = newLayoutSetPrototype.getLayoutSet();
		prototypeLayoutSet.setFaviconFileEntryId(
			faviconFileEntry.getFileEntryId());
		
		_layoutSetLocalService.updateLayoutSet(prototypeLayoutSet);

		// Create a site from the template
		ServiceContext serviceContext = ServiceContextTestUtil.getServiceContext();
		serviceContext.setAttribute(
			"layoutSetPrototypeLinkEnabled", Boolean.TRUE);
		serviceContext.setAttribute(
			"layoutSetPrototypeUuid", newLayoutSetPrototype.getUuid());

		Group newGroup = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, serviceContext);

		// Apply site template (this mimics what the site admin web portlet does)
		_sites.updateLayoutSetPrototypesLinks(
			newGroup, newLayoutSetPrototype.getLayoutSetPrototypeId(), 0, true, false);

		// Verify that the site has the template's favicon initially
		LayoutSet siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			newGroup.getGroupId(), false);
		
		Assert.assertTrue(
			"Site should have inherited favicon from template during initial creation",
			siteLayoutSet.getFaviconFileEntryId() > 0);
		
		Assert.assertEquals(
			"Site favicon should match template favicon",
			faviconFileEntry.getFileEntryId(),
			siteLayoutSet.getFaviconFileEntryId());
			
		// Now manually change the site's favicon
		FileEntry newSiteFaviconFileEntry = _addFileEntry(_getDifferentFaviconBytes());
		_layoutSetLocalService.updateFaviconFileEntryId(
			newGroup.getGroupId(), false, newSiteFaviconFileEntry.getFileEntryId());
		
		// Change template favicon
		FileEntry newTemplateFaviconFileEntry = _addFileEntry(_getFaviconBytes());
		prototypeLayoutSet.setFaviconFileEntryId(
			newTemplateFaviconFileEntry.getFileEntryId());
		_layoutSetLocalService.updateLayoutSet(prototypeLayoutSet);
		
		// Propagate changes from template to site
		_propagateChanges(newGroup);
		
		// Verify that the site's favicon was NOT changed (because it was manually set)
		siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			newGroup.getGroupId(), false);
		
		Assert.assertEquals(
			"Site favicon should not have been changed after manual modification",
			newSiteFaviconFileEntry.getFileEntryId(),
			siteLayoutSet.getFaviconFileEntryId());
	}

	@Test
	public void testFaviconInitiallyCopiedFromTemplate() throws Exception {
		// Enable favicon propagation for this test
		TestPropsUtil.set(
			PropsKeys.LAYOUT_SET_PROTOTYPE_PROPAGATE_FAVICON, "true");

		// Create a new layout set prototype with favicon
		LayoutSetPrototype newLayoutSetPrototype = 
			LayoutSetPrototypeLocalServiceUtil.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				_createLayoutSetPrototypeNameMap(), null, true, true,
				ServiceContextTestUtil.getServiceContext());

		// Add favicon to the new prototype
		FileEntry faviconFileEntry = _addFileEntry(_getFaviconBytes());
		
		LayoutSet prototypeLayoutSet = newLayoutSetPrototype.getLayoutSet();
		prototypeLayoutSet.setFaviconFileEntryId(
			faviconFileEntry.getFileEntryId());
		
		_layoutSetLocalService.updateLayoutSet(prototypeLayoutSet);

		// Create a site from the template
		ServiceContext serviceContext = ServiceContextTestUtil.getServiceContext();
		serviceContext.setAttribute(
			"layoutSetPrototypeLinkEnabled", Boolean.TRUE);
		serviceContext.setAttribute(
			"layoutSetPrototypeUuid", newLayoutSetPrototype.getUuid());

		Group newGroup = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, serviceContext);

		// Apply site template (this mimics what the site admin web portlet does)
		_sites.updateLayoutSetPrototypesLinks(
			newGroup, newLayoutSetPrototype.getLayoutSetPrototypeId(), 0, true, false);

		// Verify that the site has the template's favicon initially
		LayoutSet siteLayoutSet = _layoutSetLocalService.getLayoutSet(
			newGroup.getGroupId(), false);
		
		Assert.assertTrue(
			"Site should have inherited favicon from template",
			siteLayoutSet.getFaviconFileEntryId() > 0);
	}

	private FileEntry _addFileEntry(byte[] bytes) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_PNG, bytes, null,
			null, null, ServiceContextTestUtil.getServiceContext());
	}

	private Map<Locale, String> _createLayoutSetPrototypeNameMap() {
		return HashMapBuilder.put(
			LocaleUtil.getDefault(), 
			"Layout Set Prototype " + RandomTestUtil.randomString()
		).build();
	}

	private byte[] _getFaviconBytes() {
		// Simple 1x1 red pixel PNG
		return new byte[] {
			(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
			0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
			0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
			0x08, 0x02, 0x00, 0x00, 0x00, (byte)0x90, 0x77, 0x53,
			(byte)0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
			0x54, 0x08, (byte)0xD7, 0x63, (byte)0xF8, 0x0F, 0x00, 0x00,
			0x01, 0x01, 0x01, 0x00, 0x27, (byte)0xDB, 0x72, 0x0E,
			0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
			(byte)0xAE, 0x42, 0x60, (byte)0x82
		};
	}

	private byte[] _getDifferentFaviconBytes() {
		// Simple 1x1 blue pixel PNG
		return new byte[] {
			(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
			0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
			0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
			0x08, 0x02, 0x00, 0x00, 0x00, (byte)0x90, 0x77, 0x53,
			(byte)0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
			0x54, 0x08, (byte)0xD7, 0x63, 0x60, (byte)0xF8, 0x0F, 0x00,
			0x00, 0x01, 0x01, 0x01, 0x00, 0x1B, 0x2C, 0x45,
			0x1C, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E,
			0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
		};
	}

	private void _propagateChanges(Group group) throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		// Ensure the layout set is linked to the prototype
		if (Validator.isNull(layoutSet.getLayoutSetPrototypeUuid())) {
			layoutSet.setLayoutSetPrototypeUuid(_layoutSetPrototype.getUuid());
			layoutSet.setLayoutSetPrototypeLinkEnabled(true);
			_layoutSetLocalService.updateLayoutSet(layoutSet);
		}

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		_sites.mergeLayoutSetPrototypeLayouts(group, layoutSet);

		Thread.sleep(2000);

		// Verify merge completed successfully
		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		LayoutSet layoutSetPrototypeLayoutSet =
			layoutSetPrototype.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsUnicodeProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			layoutSetPrototypeSettingsUnicodeProperties.getProperty(
				Sites.MERGE_FAIL_COUNT));

		Assert.assertEquals(0, mergeFailCount);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	private Group _layoutSetPrototypeGroup;

	@Inject
	private Sites _sites;

}