package it.dsestili.mywebproject.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import it.dsestili.chkpwd.CheckPassword;

public class GenerateAndDownloadHashWSLogin 
{
	private static final Logger logger = Logger.getLogger(GenerateAndDownloadHashWSLogin.class);
	
	public Result generateAndDownloadHashLogin(String folder, String algorithm, String modeParam, String userName, String password)
	{
		Result r = null;
		GenerateAndDownloadHashWS ws = null;
		
		try
		{
			CheckPassword chkPwd = new CheckPassword();
			String baseDir = chkPwd.checkPassword(userName, password);
			if(baseDir == null)
			{
				return r;
			}
			
			ws = new GenerateAndDownloadHashWS();
			ws.openConnection();

			String queryGetTokenAuth = ws.getProperty("query.getTokenAuth");
			
			PreparedStatement statement = ws.connection.prepareStatement(queryGetTokenAuth);
			statement.setString(1, baseDir);
			ResultSet rs = statement.executeQuery();
			if(rs.next())
			{
				String token = rs.getString(1);
				r = ws.generateAndDownloadHash(folder, algorithm, modeParam, token);
			}
		}
		catch(Exception e)
		{
			logger.debug(e);
		}
		finally
		{
			if(ws != null && ws.connection != null)
			{
				try 
				{
					ws.connection.close();
					ws.connection = null;
					logger.debug("Connessione chiusa");
				} 
				catch(SQLException e)
				{
					logger.debug("Errore di chiusura connessione", e);
				}
			}
		}
		
		return r;
	}
}
