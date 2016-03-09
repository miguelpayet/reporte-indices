package excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class Excel {

	private ExcelConfig cfg;
	private Workbook wb;

	public Excel(ExcelConfig cfg) {
		this.cfg = cfg;
		wb = new SXSSFWorkbook(100);
	}

	public Sheet getCurrentSheet(String sName) {
		Sheet currentSheet = wb.getSheet(sName);
		if (currentSheet == null) {
			currentSheet = wb.createSheet(sName);
		}
		return currentSheet;
	}

	public void grabar() throws ExcelException {
		try {
			FileOutputStream out = new FileOutputStream(cfg.getRuta() + cfg.getNombreArchivo());
			wb.write(out);
			out.close();
		} catch (IOException e) {
			throw new ExcelException("grabar - " + e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

}