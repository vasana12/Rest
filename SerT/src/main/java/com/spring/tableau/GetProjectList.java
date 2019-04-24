package com.spring.tableau;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import com.spring.tableau.Entity.MultiLaunchWbEntity;
import com.spring.tableau.bindings.ProjectListType;
import com.spring.tableau.bindings.TableauCredentialsType;
import com.spring.tableau.bindings.WorkbookType;
import com.spring.tableau.util.RestApiUtils;

public class GetProjectList {
	private static Logger s_logger = Logger.getLogger(GetWorkbooksList.class);
	private static Properties s_properties = new Properties();

	private static final RestApiUtils s_restApiUtils = RestApiUtils.getInstance();

	static {
		// Configures the logger to log to stdout
		BasicConfigurator.configure();

		// Loads the values from configuration file into the Properties instance
		try {
			s_properties.load(new ClassPathResource("config.properties").getInputStream());
		} catch (IOException e) {
			s_logger.error("Failed to load configuration files.");
		}
	}
	
	public  List<MultiLaunchWbEntity> getPjList(){
		
		String username = s_properties.getProperty("user.admin.name");
		String password = s_properties.getProperty("user.admin.password");
		String contentUrl = s_properties.getProperty("site.default.contentUrl");

		// Signs in to server and saves the authentication token, site ID, and
		// current user ID
		TableauCredentialsType credential = s_restApiUtils.invokeSignIn(
				username, password, contentUrl);
		String currentSiteId = credential.getSite().getId();
		String currentUserId = credential.getUser().getId();

		s_logger.info(String.format("Authentication token: %s",
				credential.getToken()));
		s_logger.info(String.format("Site ID: %s", currentSiteId));
		ProjectListType projects = s_restApiUtils.invokeQueryProjects(
				credential, currentSiteId);
		List<WorkbookType> currentUserWorkbooks = s_restApiUtils
				.invokeQueryWorkbooks(credential, currentSiteId, currentUserId)
				.getWorkbook();
		int i;
		List<MultiLaunchWbEntity> wbList = new ArrayList<MultiLaunchWbEntity>();
		
		for (i = 0; i < currentUserWorkbooks.size() - 1; i++) {
			WorkbookType WorkbookTypeObj = currentUserWorkbooks.get(i);
			MultiLaunchWbEntity wbObj = new MultiLaunchWbEntity();
			wbObj.setProjectName(WorkbookTypeObj.getProject().getName());
			wbObj.setReportName(WorkbookTypeObj.getName());
			wbList.add(wbObj);
		}
		
		return wbList;
	}

}
