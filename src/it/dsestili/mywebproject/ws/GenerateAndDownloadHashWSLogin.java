/*
GenerateAndDownloadHashWSLogin
Copyright (C) 2017 Davide Sestili

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
