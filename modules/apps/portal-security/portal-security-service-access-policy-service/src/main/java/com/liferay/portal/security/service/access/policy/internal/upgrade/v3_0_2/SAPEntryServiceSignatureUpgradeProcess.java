/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.internal.upgrade.v3_0_2;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Javier Gamarra
 */
public class SAPEntryServiceSignatureUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateObjectEntryRelationshipServiceSignatures();
	}

	private void _updateObjectEntryRelationshipServiceSignatures()
		throws Exception {

		String oldSignature =
			"com.liferay.object.rest.internal.resource.v1_0." +
				"ObjectEntryResourceImpl#" +
					"putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode";

		String newSignature =
			"com.liferay.object.rest.internal.resource.v1_0." +
				"ObjectEntryRelatedObjectsResourceImpl#" +
					"putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode";

		try (PreparedStatement selectPS = connection.prepareStatement(
				 "SELECT sapEntryId, allowedServiceSignatures FROM SAPEntry " +
				 "WHERE allowedServiceSignatures LIKE ?")) {

			selectPS.setString(1, "%" + oldSignature + "%");

			try (ResultSet rs = selectPS.executeQuery()) {
				while (rs.next()) {
					long sapEntryId = rs.getLong("sapEntryId");
					String allowedServiceSignatures = rs.getString(
						"allowedServiceSignatures");

					String updatedSignatures = StringUtil.replace(
						allowedServiceSignatures, oldSignature, newSignature);

					_updateSAPEntry(sapEntryId, updatedSignatures);
				}
			}
		}
	}

	private void _updateSAPEntry(
			long sapEntryId, String allowedServiceSignatures)
		throws Exception {

		try (PreparedStatement ps = connection.prepareStatement(
				"UPDATE SAPEntry SET allowedServiceSignatures = ? " +
				"WHERE sapEntryId = ?")) {

			ps.setString(1, allowedServiceSignatures);
			ps.setLong(2, sapEntryId);

			ps.executeUpdate();
		}
	}

} 