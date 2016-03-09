package main;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.sql.*;

public class Consultador implements Runnable {

	private Connection conn;
	private Sheet excelSheet;
	private int filaExcel;
	private PreparedStatement pstmtCursor;

	public Consultador(Connection unaConexion, String consulta, String condicion) throws ExcepcionConsultador {
		try {
			this.conn = unaConexion;
			prepararSentencias(String.format("%s %s", consulta, condicion));
		} catch (Exception e) {
			throw new ExcepcionConsultador(e.getMessage(), e);
		}
	}

	private void cerrarConexion() throws ExcepcionConsultador {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - cerrarConexion: " + e.getMessage(), e);
		}
	}

	private void consultar() throws ExcepcionConsultador {
		final int QUIEBRE_LOG = 50000;
		try {
			try {
				ResultSet rsetCursor = pstmtCursor.executeQuery();
				ResultSetMetaData rsmdInd = rsetCursor.getMetaData();
				grabarHeader(rsmdInd);
				try {
					while (rsetCursor.next()) {
						Row row = excelSheet.createRow(filaExcel++);
						if ((filaExcel % QUIEBRE_LOG) == 0) {
							ConsultadorAplicacion.getLogger().info(String.format("%s - %s", excelSheet.getSheetName(), filaExcel));
						}
						grabarColumnas(row, rsetCursor, rsmdInd, 0);
					}
				} finally {
					ConsultadorAplicacion.getLogger().info(String.format("fin %s - %s", excelSheet.getSheetName()));
					rsetCursor.close();
				}
			} finally {
				pstmtCursor.close();
			}
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - consultar: " + e.getMessage(), e);
		}
	}

	private int grabarColumnas(Row row, ResultSet rset, ResultSetMetaData rsmd, int columna) throws ExcepcionConsultador {
		try {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				Cell celda = row.createCell(columna++);
				switch (rsmd.getColumnType(i)) {
					case Types.CHAR:
						celda.setCellValue(rset.getString(i));
						break;
					case Types.VARCHAR:
						celda.setCellValue(rset.getString(i));
						break;
					case Types.NUMERIC:
						celda.setCellValue(rset.getFloat(i));
						break;
					case Types.DATE:
						celda.setCellValue(rset.getDate(i));
						break;
					case Types.TIMESTAMP:
						celda.setCellValue(rset.getDate(i));
						break;
				}
			}
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - grabarColumnas: " + e.getMessage(), e);
		}
		return columna;
	}

	private void grabarHeader(ResultSetMetaData rsmdInd) throws ExcepcionConsultador {
		Row row = excelSheet.createRow(filaExcel++);
		grabarTitulos(row, rsmdInd, 0);
	}

	private int grabarTitulos(Row row, ResultSetMetaData rsmd, int columna) throws ExcepcionConsultador {
		try {
			for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
				Cell celda = row.createCell(columna++);
				celda.setCellValue(rsmd.getColumnName(i));
			}
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - grabarTitulos: " + e.getMessage(), e);
		}
		return columna;
	}

	private void prepararSentencias(String unQuery) throws ExcepcionConsultador {
		try {
			pstmtCursor = conn.prepareStatement(unQuery);
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - prepararSentencias: " + e.getMessage(), e);
		}
	}

	public void run() {
		try {
			consultar();
		} catch (ExcepcionConsultador e) {
			ConsultadorAplicacion.getLogger().error(String.format("Error al ejecutar consulta: %s ", e.getMessage()));
		} finally {
			try {
				cerrarConexion();
			} catch (ExcepcionConsultador e) {
				ConsultadorAplicacion.getLogger().error(String.format("Error al cerrar conexión: %s ", e.getMessage()));
			}
		}
	}

	public void setExcel(Sheet unExcel) {
		this.excelSheet = unExcel;
	}

}
