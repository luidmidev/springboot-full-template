package com.luidmidev.template.spring.services.reports;

import lombok.Getter;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Clase base para trabajar con archivos de Excel.
 */
@Getter
public class ExcelManager implements Closeable {


    protected XSSFWorkbook workbook;

    protected XSSFFormulaEvaluator evaluator;

    /**
     * Constructor para crear un objeto ExcelModel a partir de una ruta de archivo.
     *
     * @param filePath la ruta del archivo de Excel
     * @throws IOException si el archivo no se encuentra o no se puede leer
     */
    public ExcelManager(String filePath) throws IOException {
        this(new FileInputStream(filePath));
    }

    /**
     * Constructor para crear un objeto ExcelModel a partir de un arreglo de bytes.
     *
     * @param file el arreglo de bytes que contiene los datos del archivo de Excel
     * @throws IOException si el arreglo de bytes no contiene datos de un archivo de Excel
     */
    public ExcelManager(byte[] file) throws IOException {
        this(new ByteArrayInputStream(file));

    }

    /**
     * Constructor para crear un objeto ExcelModel a partir de un objeto XSSFWorkbook.
     */
    public ExcelManager(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    /**
     * Constructor para crear un objeto ExcelModel a partir de un objeto InputStream.
     *
     * @param inputStream el objeto InputStream que contiene los datos del archivo de Excel
     * @throws IOException si el objeto InputStream no contiene datos de un archivo de Excel
     */
    public ExcelManager(InputStream inputStream) throws IOException {
        this(new XSSFWorkbook(inputStream));
    }


    /**
     * Obtiene un array de celdas asociadas a un nombre de celda dado.
     *
     * @param name el nombre de la celda
     * @return un array de celdas asociadas al nombre de celda dado
     */
    public XSSFCell[] getCellsFromName(String name) {
        CellReference[] cellsReferences = getCellsReferencesFromName(name);
        return CellReferenceToXSSFCell(cellsReferences);
    }


    /**
     * Obtiene un array de celdas asociadas a una formula dada.
     *
     * @param formula la formula
     * @return un array de celdas asociadas a la formula dada
     */
    public XSSFCell[] getCellsFromFormula(String formula) {
        CellReference[] cellsReferences = getCellsReferencesFromFormula(formula);
        return CellReferenceToXSSFCell(cellsReferences);
    }


    /**
     * Obtiene un array de celdas asociadas a un nombre de celda dado.
     *
     * @param name el nombre de la celda
     * @return un array de celdas asociadas al nombre de celda dado
     */
    public XSSFCell getCellFromName(String name) throws Exception {
        XSSFCell[] cells = getCellsFromName(name);
        if (cells.length > 1) {
            throw new Exception("El nombre " + name + " debe hacer referencia a una unica celda");
        }
        return cells[0];
    }

    /**
     * Obtiene un array de celdas asociadas a una formula dada.
     *
     * @param formula la formula
     * @return un array de celdas asociadas a la formula dada
     */
    public XSSFCell getCellFromFormula(String formula) throws Exception {
        XSSFCell[] cells = getCellsFromFormula(formula);
        if (cells.length > 1) {
            throw new Exception("La formula " + formula + " debe hacer referencia a una unica celda");
        }
        return cells[0];
    }


    /**
     * Obtiene un array de celdas asociadas a un nombre de celda dado.
     *
     * @param cellsReferences array de referencias de celdas
     * @return un array de celdas asociadas al nombre de celda dado
     */
    private XSSFCell[] CellReferenceToXSSFCell(CellReference[] cellsReferences) {
        XSSFCell[] cells = new XSSFCell[cellsReferences.length];

        for (int i = 0; i < cellsReferences.length; i++) {
            cells[i] = getCellFromCellReference(cellsReferences[i]);
        }

        return cells;
    }

    public XSSFCell[] filterCells(XSSFCell[] cells, Predicate<XSSFCell> predicate) {
        return Arrays.stream(cells).filter(predicate).toArray(XSSFCell[]::new);
    }


    /**
     * Devuelve la celda correspondiente a la referencia de celda especificada.
     *
     * @param reference La referencia de celda que se utilizará para buscar la celda correspondiente.
     * @return La celda correspondiente a la referencia de celda especificada.
     */
    public XSSFCell getCellFromCellReference(CellReference reference) {
        XSSFSheet sxxfSheet = workbook.getSheet(reference.getSheetName());
        XSSFRow row = sxxfSheet.getRow(reference.getRow());
        return row.getCell(reference.getCol());
    }


    public XSSFCell getCellFromIndex(int sheetIndex, int rowIndex, int cellIndex) throws Exception {
        XSSFSheet sxxfSheet = workbook.getSheetAt(sheetIndex);
        if (sxxfSheet == null)
            throw new Exception("No existe la hoja de calculo con indice " + sheetIndex);

        XSSFRow row = sxxfSheet.getRow(rowIndex);
        return row.getCell(cellIndex);
    }

    public XSSFCell getCellFromIndex(String sheetName, int rowIndex, int cellIndex) throws Exception {
        XSSFSheet sxxfSheet = workbook.getSheet(sheetName);
        if (sxxfSheet == null)
            throw new Exception("No existe la hoja de calculo con nombre " + sheetName);
        //verificar que la fila exista
        if (sxxfSheet.getRow(rowIndex) == null)
            sxxfSheet.createRow(rowIndex);

        XSSFRow row = sxxfSheet.getRow(rowIndex);
        return row.getCell(cellIndex);
    }


    /**
     * Obtiene los valores de celda correspondientes al nombre de celda especificado como una cadena.
     *
     * @param name el nombre de celda a buscar en la hoja de cálculo.
     * @return un array de String que contiene los valores de celda correspondientes al nombre de celda
     * especificado.
     * @see #getCellsValues(XSSFCell[] cells)
     * @see #getCellsFromName(String name)
     */
    public String[] getValuesCellsFromName(String name) {
        return getCellsValuesWithEvaluator(getCellsFromName(name), evaluator);
    }

    /**
     * Obtiene un arreglo de objetos CellReference que representan las celdas incluidas en el
     * rango con nombre especificado en el libro de trabajo.
     *
     * @param name el nombre del rango de celdas para el cual se desea obtener los objetos CellReference
     * @return un arreglo de objetos CellReference que representan las celdas incluidas en el rango
     * con nombre especificado
     * @throws NullPointerException si el parámetro "name" es nulo
     */
    public CellReference[] getCellsReferencesFromName(String name) {
        XSSFName xssfName = workbook.getName(name);
        String formula = xssfName.getRefersToFormula();
        return getCellsReferencesFromFormula(formula);
    }


    public CellReference[] getCellsReferencesFromFormula(String formula) {
        AreaReference areaReference = new AreaReference(formula, workbook.getSpreadsheetVersion());
        return areaReference.getAllReferencedCells();
    }

    /**
     * Obtiene el valor de una única celda a partir de su nombre.
     *
     * @param name El nombre de la celda.
     * @return El valor de la celda especificada.
     * @throws Exception Si el nombre hace referencia a más de una celda.
     */
    public String getValueCellFromName(String name) throws Exception {
        String[] title = getValuesCellsFromName(name);
        if (title.length > 1)
            throw new Exception("El nombre " + name + " debe hacer referencia a una unica celda");
        return title[0];
    }

    /**
     * Obtiene los valores de las celdas especificadas y los devuelve como un arreglo
     * de cadenas.
     *
     * @param cells el arreglo de objetos Cell que contiene las celdas de las que se
     *              desea obtener los valores
     * @return un arreglo de cadenas que contiene los valores de cada celda en el mismo
     * orden que en el arreglo de entrada
     * @see #getCellValue(XSSFCell name)
     */
    public static String[] getCellsValues(XSSFCell[] cells) throws Exception {
        String[] cellValues = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            cellValues[i] = getCellValue(cells[i]);
        }
        return cellValues;
    }

    /**
     * Obtiene los valores de las celdas especificadas utilizando un evaluador de fórmulas y los devuelve como un arreglo de cadenas de caracteres.
     *
     * @param cells     Arreglo de celdas de las que se desean obtener los valores.
     * @param evaluator Evaluador de fórmulas utilizado para obtener el valor de las celdas de tipo fórmula.
     * @return Arreglo de cadenas de caracteres que contiene los valores de las celdas.
     * @see #getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator)
     */
    public static String[] getCellsValuesWithEvaluator(XSSFCell[] cells, XSSFFormulaEvaluator evaluator) {
        String[] cellValues = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            cellValues[i] = getCellValueWithEvaluator(cells[i], evaluator);
        }
        return cellValues;
    }

    /**
     * Obtiene el valor de la celdas especificadas y lo delvuelve como cadena de caracteres
     *
     * @param cell Celda de las que se desea obtener el valor
     * @return Valor de la celda como cadena de caracteres
     * @see #getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator)
     * @see #getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator, int decimals)
     */
    public static String getCellValue(XSSFCell cell) {
        String cellValue = "";
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING -> cellValue = cell.getStringCellValue();
            case NUMERIC -> cellValue = String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> cellValue = String.valueOf(cell.getBooleanCellValue());
            case BLANK -> cellValue = "";
            case ERROR -> cellValue = "Error";
            case FORMULA -> cellValue = cell.getCellFormula();
        }
        return cellValue;
    }

    /**
     * Obtiene el valor de la celdas especificadas y lo delvuelve como cadena de caracteres
     *
     * @param cellValue CellValue de las que se desea obtener el valor
     * @return Valor de la celda como cadena de caracteres
     * @see #getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator)
     * @see #getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator, int decimals)
     */
    public static String getCellValue(CellValue cellValue) {
        String cellVal = "";
        if (cellValue == null) return "";

        switch (cellValue.getCellType()) {
            case STRING -> cellVal = cellValue.getStringValue();
            case NUMERIC -> cellVal = String.valueOf(cellValue.getNumberValue());
            case BOOLEAN -> cellVal = String.valueOf(cellValue.getBooleanValue());
            case ERROR -> cellVal = "Error";
        }
        return cellVal;
    }

    /**
     * Obtiene el valor de la celda especificada utilizando un evaluador de fórmulas y lo devuelve como cadena de caracteres.
     *
     * @param cell      Celda de la que se desea obtener el valor.
     * @param evaluator Evaluador de fórmulas a utilizar.
     * @return Valor de la celda como cadena de caracteres.
     */
    public static String getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator) {
        String cellValue = "";
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING -> cellValue = cell.getStringCellValue();
            case NUMERIC -> cellValue = String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> cellValue = String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cellValue = getCellValue(evaluator.evaluate(cell));
            case BLANK -> cellValue = "";
            case ERROR -> cellValue = "Error";
        }
        return cellValue;
    }

    /**
     * Obtiene el valor de la celda especificada utilizando un evaluador de fórmulas y lo devuelve como cadena de caracteres
     * con el número de decimales especificado.
     *
     * @param cell      Celda de la que se desea obtener el valor.
     * @param evaluator Evaluador de fórmulas a utilizar.
     * @param decimals  Número de decimales a mostrar.
     * @return Valor de la celda con el número de decimales especificado como cadena de caracteres.
     * @throws Exception Si la celda no devuelve un valor del tipo numerico.
     */
    public static String getCellValueWithEvaluator(XSSFCell cell, XSSFFormulaEvaluator evaluator, int decimals) throws Exception {
        String cellValue = "";
        DecimalFormat df = new DecimalFormat("#." + "#".repeat(Math.max(0, decimals)));

        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING, BOOLEAN, BLANK -> throw new Exception("La Celda no contiene un valor numerico.");
            case NUMERIC -> cellValue = df.format(cell.getNumericCellValue());
            case FORMULA -> cellValue = getCellValueWithEvaluator(evaluator.evaluateInCell(cell), evaluator, 2);
            case ERROR -> cellValue = "Error";
        }
        return cellValue;
    }

    /**
     * Devuelve una cadena que representa la referencia completa de una celda, incluyendo el nombre de la hoja.
     *
     * @param cell La celda de la cual se desea obtener la referencia completa.
     * @return Una cadena que representa la referencia completa de la celda en el formato "nombreHoja!referenciaCelda".
     */
    public static String getFullCellReferenceAsString(XSSFCell cell) {
        int rowIndex = cell.getRowIndex();
        int colIntex = cell.getColumnIndex();
        String sheetName = "'" + cell.getSheet().getSheetName() + "'";
        String cellReference = new CellReference(rowIndex, colIntex, true, true).formatAsString();
        return sheetName + "!" + cellReference;
    }

    /**
     * Devuelve un arreglo de cadenas que representan la referencia completa de las celdas especificadas, incluyendo el nombre de la hoja.
     *
     * @param cells Las celdas de las cuales se desea obtener la referencia completa.
     * @return Un arreglo de cadenas que representan la referencia completa de las celdas en el formato "nombreHoja!referenciaCelda".
     */
    public static String[] getFullCellReferenceAsString(XSSFCell[] cells) {
        String[] cellReferences = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            cellReferences[i] = getFullCellReferenceAsString(cells[i]);
        }
        return cellReferences;
    }

    /**
     * Obtiene la celda correspondiente a una referencia completa de celda en formato de cadena.
     *
     * @param fullCellReference La referencia completa de la celda en el formato "nombreHoja!referenciaCelda".
     * @return La celda correspondiente a la referencia completa especificada.
     */
    public XSSFCell getCellFromFullReference(String fullCellReference) {
        CellReference cellReference = new CellReference(fullCellReference);
        XSSFSheet sheet = workbook.getSheet(cellReference.getSheetName());
        XSSFRow row = sheet.getRow(cellReference.getRow());
        return row.getCell(cellReference.getCol());
    }

    /**
     * Obtiene un objeto ByteArrayInputStream a partir del Workbook actual.
     *
     * @return Objeto ByteArrayInputStream que contiene los datos del Workbook.
     * @throws IOException Si ocurre un error al escribir en el ByteArrayOutputStream o al crear el ByteArrayInputStream.
     */
    public ByteArrayInputStream getInputStreamFromWorkbook() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] bytes = bos.toByteArray();
        bos.close();
        return new ByteArrayInputStream(bytes);
    }

    public ExcelManager cloneManageExcel() throws IOException {
        InputStream inputStream = getInputStreamFromWorkbook();
        return new ExcelManager(inputStream);
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        workbook.close();
    }
}
