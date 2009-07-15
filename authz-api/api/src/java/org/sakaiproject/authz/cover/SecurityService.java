/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.authz.cover;

import org.sakaiproject.component.cover.ComponentManager;

/**
 * <p>
 * SecurityService is a static Cover for the {@link org.sakaiproject.authz.api.SecurityService SecurityService}; see that interface for usage details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SecurityService
{
	/**
	 * Access the component instance: special cover only method.
	 * 
	 * @return the component instance.
	 */
	public static org.sakaiproject.authz.api.SecurityService getInstance()
	{
		if (ComponentManager.CACHE_COMPONENTS)
		{
			if (m_instance == null)
				m_instance = (org.sakaiproject.authz.api.SecurityService) ComponentManager
						.get(org.sakaiproject.authz.api.SecurityService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.authz.api.SecurityService) ComponentManager
					.get(org.sakaiproject.authz.api.SecurityService.class);
		}
	}

	private static org.sakaiproject.authz.api.SecurityService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.authz.api.SecurityService.SERVICE_NAME;

	public static java.util.List unlockUsers(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return null;

		return service.unlockUsers(param0, param1);
	}

	public static boolean isSuperUser()
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.isSuperUser();
	}

	public static boolean isSuperUser(java.lang.String param0)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.isSuperUser(param0);
	}

	public static boolean unlock(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.unlock(param0, param1);
	}

	public static boolean unlock(org.sakaiproject.user.api.User param0, java.lang.String param1, java.lang.String param2)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.unlock(param0, param1, param2);
	}

	public static boolean unlock(java.lang.String param0, java.lang.String param1, java.lang.String param2)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.unlock(param0, param1, param2);
	}

	public static boolean unlock(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.util.Collection param3)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.unlock(param0, param1, param2, param3);
	}

	public static void pushAdvisor(org.sakaiproject.authz.api.SecurityAdvisor param0)
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return;

		service.pushAdvisor(param0);
	}

	public static org.sakaiproject.authz.api.SecurityAdvisor popAdvisor()
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return null;

		return service.popAdvisor();
	}

	public static boolean hasAdvisors()
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return false;

		return service.hasAdvisors();
	}

	public static void clearAdvisors()
	{
		org.sakaiproject.authz.api.SecurityService service = getInstance();
		if (service == null) return;

		service.clearAdvisors();
	}
}