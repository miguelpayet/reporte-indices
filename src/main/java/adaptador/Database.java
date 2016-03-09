package adaptador;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import main.ConsultadorAplicacion;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.Locale;

class Database {

	private final static String CONN_STRING = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=%1s)(PORT = 1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = %2s)))";
	private String connString;
	private ComboPooledDataSource cpds;
	private CuentaDatabase cuenta;

	Database(CuentaDatabase unaCuenta) {
		this.cuenta = unaCuenta;
	}

	void close() {
		cpds.close();
	}

	void connect() throws DatabaseException {
		setearConnString();
		crearDataSource();
		ejecutarQueryInicial();
	}

	private void crearDataSource() throws DatabaseException {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("oracle.jdbc.OracleDriver");
		} catch (PropertyVetoException e) {
			throw new DatabaseException("crearDataSource - PropertyVetoException: " + e.getMessage(), e);
		}
		ConsultadorAplicacion.getLogger().info(connString);
		cpds.setJdbcUrl(connString);
		cpds.setUser(cuenta.getUsuario());
		cpds.setPassword(cuenta.getPassword());
		cpds.setAcquireRetryAttempts(2);
	}

	private void ejecutarQueryInicial() throws DatabaseException {
		try {
			try (Connection conn = getConnection()) {
				try (PreparedStatement pstmt = conn.prepareStatement("select BANNER from SYS.V_$VERSION")) {
					try (ResultSet rset = pstmt.executeQuery()) {
						while (rset.next()) {
							ConsultadorAplicacion.getLogger().info(rset.getString(1));
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("SQLException: " + e.getMessage(), e);
		}
	}

	Connection getConnection() throws DatabaseException {
		Connection conn;
		try {
			conn = cpds.getConnection();
		} catch (SQLException e) {
			throw new DatabaseException("getConnection - SQLException: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new DatabaseException("getConnection - Exception: " + e.getMessage(), e);
		}
		return conn;
	}


	@SuppressWarnings("unused")
	public PreparedStatement getStatement(String strSql) throws DatabaseException {
		try {
			return getConnection().prepareStatement(strSql);
		} catch (SQLException e) {
			throw new DatabaseException("getStatement - SQLException: " + e.getMessage(), e);
		}
	}

	private void setearConnString() {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(Database.CONN_STRING, cuenta.getHost(), cuenta.getService());
		connString = sb.toString();
	}
}
