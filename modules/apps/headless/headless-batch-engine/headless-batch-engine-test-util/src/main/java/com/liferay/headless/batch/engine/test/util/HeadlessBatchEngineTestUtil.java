package com.liferay.headless.batch.engine.test.util;

import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.resource.v1_0.ExportTaskResource;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;
import org.junit.Assert;

public class HeadlessBatchEngineTestUtil {
    public static ImportTask waitForFinish(
        String expectedExecuteStatus,
        String importTaskJSON,
        Company testCompany
    )
            throws Exception {

        ImportTask importTask = ImportTask.toDTO(importTaskJSON);

        User testCompanyAdminUser = UserTestUtil.getAdminUser(
                testCompany.getCompanyId());

        ImportTaskResource importTaskResource = ImportTaskResource.builder(
        ).authentication(
                testCompanyAdminUser.getEmailAddress(),
                PropsValues.DEFAULT_ADMIN_PASSWORD
        ).endpoint(
                testCompany.getVirtualHostname(), 8080, "http"
        ).locale(
                LocaleUtil.getDefault()
        ).build();

        while (true) {
            importTask = importTaskResource.getImportTask(importTask.getId());

            if (StringUtil.equals(
                    importTask.getExecuteStatusAsString(), "COMPLETED") ||
                    StringUtil.equals(
                            importTask.getExecuteStatusAsString(), "FAILED")) {

                Assert.assertEquals(
                        expectedExecuteStatus, importTask.getExecuteStatusAsString());
                break;
            }
        }
        return  importTask;
    }

//    public static ImportTask waitForFinish(
//            String expectedExecuteStatus,
//            ImportTask importTask,
//            Company testCompany
//    )
//            throws Exception {
//
//        User testCompanyAdminUser = UserTestUtil.getAdminUser(
//                testCompany.getCompanyId());
//
//        ImportTaskResource importTaskResource = ImportTaskResource.builder(
//        ).authentication(
//                testCompanyAdminUser.getEmailAddress(),
//                PropsValues.DEFAULT_ADMIN_PASSWORD
//        ).endpoint(
//                testCompany.getVirtualHostname(), 8080, "http"
//        ).locale(
//                LocaleUtil.getDefault()
//        ).build();
//
//        while (true) {
//            importTask = importTaskResource.getImportTask(importTask.getId());
//
//            if (StringUtil.equals(
//                    importTask.getExecuteStatusAsString(), "COMPLETED") ||
//                    StringUtil.equals(
//                            importTask.getExecuteStatusAsString(), "FAILED")) {
//
//                Assert.assertEquals(
//                        expectedExecuteStatus, importTask.getExecuteStatusAsString());
//                break;
//            }
//        }
//        return  importTask;
//    }
//
//    public static ExportTask waitForFinish(
//            String expectedExecuteStatus,
//            ExportTask exportTask,
//            ExportTaskResource exportTaskResource
//    )
//            throws Exception {
//
//        while (true) {
//            exportTask = exportTaskResource.getExportTask(exportTask.getId());
//            if (StringUtil.equals(
//                    exportTask.getExecuteStatusAsString(), "COMPLETED") ||
//                    StringUtil.equals(
//                            exportTask.getExecuteStatusAsString(), "FAILED")) {
//
//                Assert.assertEquals(
//                        expectedExecuteStatus, exportTask.getExecuteStatusAsString());
//                break;
//            }
//        }
//        return  exportTask;
//    }
//
//
//    public static ExportTask waitForFinish(
//            String expectedExecuteStatus,
//            ExportTask exportTask,
//            Company testCompany
//    )
//            throws Exception {
//
//        User testCompanyAdminUser = UserTestUtil.getAdminUser(
//                testCompany.getCompanyId());
//
//        ExportTaskResource exportTaskResource = ExportTaskResource.builder(
//        ).authentication(
//                testCompanyAdminUser.getEmailAddress(),
//                PropsValues.DEFAULT_ADMIN_PASSWORD
//        ).endpoint(
//                testCompany.getVirtualHostname(), 8080, "http"
//        ).locale(
//                LocaleUtil.getDefault()
//        ).build();
//
//        while (true) {
//
//            exportTask = exportTaskResource.getExportTask(exportTask.getId());
//            if (StringUtil.equals(
//                    exportTask.getExecuteStatusAsString(), "COMPLETED") ||
//                    StringUtil.equals(
//                            exportTask.getExecuteStatusAsString(), "FAILED")) {
//
//                Assert.assertEquals(
//                        expectedExecuteStatus, exportTask.getExecuteStatusAsString());
//                break;
//            }
//        }
//        return  exportTask;
//    }
}
