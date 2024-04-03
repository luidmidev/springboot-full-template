package com.luidmidev.template.spring.reports;

import com.aspose.cells.PaperSizeType;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.luidmidev.template.spring.services.store.FileStoreService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public final class ReportMaker {

    private ReportMaker() {
        throw new IllegalStateException("Utility class");
    }

    public static ReportFile save(ExcelManager manage, FileStoreService fileStoreService, String referencePMS, XSSFWorkbookType type) throws Exception {
        return save(manage, fileStoreService, referencePMS, new PdfSaveOptions(), type);
    }

    public static ReportFile save(ExcelManager manage, FileStoreService fileStoreService, String referencePMS, PdfSaveOptions pdfSaveOptions, XSSFWorkbookType type) throws Exception {

        var summary = new ReportFile();

        var evaluator = manage.getEvaluator();
        evaluator.clearAllCachedResultValues();
        evaluator.evaluateAll();

        var input = manage.getInputStreamFromWorkbook();
        var output = new ByteArrayOutputStream();

        var workbook = new Workbook(input);

        for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
            var sheet = workbook.getWorksheets().get(i);
            var pageSetup = sheet.getPageSetup();
            pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        }

        pdfSaveOptions.setAllColumnsInOnePagePerSheet(false);
        pdfSaveOptions.setOnePagePerSheet(false);

        workbook.save(output, pdfSaveOptions);

        var pdfBytes = output.toByteArray();

        var idFilePDF = fileStoreService.storeEphimeral(new ByteArrayInputStream(pdfBytes), "report.pdf");
        output.close();
        summary.setIdFilePDF(idFilePDF);

        var idFileExcel = fileStoreService.storeEphimeral(manage.getInputStreamFromWorkbook(), "report." + type.getExtension());
        input.close();
        summary.setIdFileExcel(idFileExcel);

        if (referencePMS != null && !referencePMS.isEmpty()) {
            Cell cells = manage.getCellFromName(referencePMS);
            summary.getData().put("pms", evaluator.evaluate(cells).getNumberValue());
        }

        manage.close();
        return summary;
    }
}
