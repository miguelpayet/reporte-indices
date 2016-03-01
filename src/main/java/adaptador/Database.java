package adaptador;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.log.MLevel;
import main.ConsultadorGeneral;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.Locale;

public class Database {

	private final static String CONN_STRING = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=%1s)(PORT = 1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = %2s)))";
	private String connString;
	private ComboPooledDataSource cpds;
	private CuentaDatabase cuenta;

	public Database() {
	}

	@SuppressWarnings("unused")
	public void connect() throws DatabaseException {
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
		ConsultadorGeneral.getLogger().info(connString);
		cpds.setJdbcUrl(connString);
		cpds.setUser(cuenta.getUsuario());
		cpds.setPassword(cuenta.getPassword());
		cpds.setAcquireRetryAttempts(2);
	}

	private void ejecutarQueryInicial() throws DatabaseException {
		try {
			Connection conn = getConnection();
			try {
				PreparedStatement pstmt = conn.prepareStatement("select BANNER from SYS.V_$VERSION");
				try {
					ResultSet rset = pstmt.executeQuery();
					try {
						while (rset.next()) {
							ConsultadorGeneral.getLogger().info(rset.getString(1));
						}
					} finally {
						rset.close();
					}
				} finally {
					pstmt.close();
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			throw new DatabaseException("SQLException: " + e.getMessage(), e);
		}
	}

	public Connection getConnection() throws DatabaseException {
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
	public CuentaDatabase getCuenta() {
		return cuenta;
	}

	@SuppressWarnings("unused")
	public PreparedStatement getStatement(String strSql) throws DatabaseException {
		try {
			return getConnection().prepareStatement(strSql);
		} catch (SQLException e) {
			throw new DatabaseException("getStatement - SQLException: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	public void setCuenta(CuentaDatabase cuenta) {
		this.cuenta = cuenta;
	}

	private void setearConnString() {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(Database.CONN_STRING, cuenta.getHost(), cuenta.getService());
		connString = sb.toString();
	}
}
