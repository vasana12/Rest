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
import com.spring.tableau.bindings.ProjectType;
import com.spring.tableau.bindings.TableauCredentialsType;
import com.spring.tableau.bindings.WorkbookType;
import com.spring.tableau.util.RestApiUtils;

public class GetWorkbooksList  {

	private static Logger s_logger = Logger.getLogger(GetWorkbooksList.class);
	public static final String multiLaunchProjectName = "기본";

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

	public List<MultiLaunchWbEntity> getWbList() {
		// Sets the username, password, and content URL, which are all required
		// in the payload of a Sign In request
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

		// Queries the projects on the current site and iterates over the list
		// to
		// find the ID of the default project
		ProjectType defaultProject = null;
		ProjectListType projects = s_restApiUtils.invokeQueryProjects(
				credential, currentSiteId);
		for (ProjectType project : projects.getProject()) {
			if (project.getName().equals("기본")
					|| project.getName().equals("기본")) {
				defaultProject = project;

				s_logger.info(String.format("Default project found: %s",
						defaultProject.getId()));
			}
		}

		// Ensure the default project was found before attempting to use it; if
		// it was not found,
		// log the failure and exit gracefully
		if (defaultProject == null) {
			s_logger.error("Failed to find default project");

			// Signs out of the server. This invalidates the authentication
			// token so
			// that it cannot be used for more requests.
			s_restApiUtils.invokeSignOut(credential);

			s_logger.info("Exiting without publishing due to previous failure");
			return null;
		}

		// Gets the list of workbooks the current user can read
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

			if (WorkbookTypeObj.getProject().getName()
					.equals(multiLaunchProjectName)) {
				s_logger.info("List of workbook of current user and project name  :"
						+ WorkbookTypeObj.getName()
						+ "and project name :"
						+ WorkbookTypeObj.getProject().getName());
				wbList.add(wbObj);
			}
		}

		// Signs out of the server. This invalidates the authentication token so
		// that it cannot be used for more requests.
		s_restApiUtils.invokeSignOut(credential);
		return wbList;
	}
}
