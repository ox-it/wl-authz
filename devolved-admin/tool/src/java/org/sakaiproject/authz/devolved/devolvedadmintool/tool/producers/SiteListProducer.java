/******************************************************************************
 * SiteListProducer.java 
 * 
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.authz.devolved.devolvedadmintool.tool.producers;

import java.util.List;

import org.sakaiproject.authz.api.DevolvedSakaiSecurity;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * This is the view producer for the HelloWorld html template
 * @author Sakai App Builder -AZ
 */
public class SiteListProducer implements ViewComponentProducer, DefaultView {

	// The VIEW_ID must match the html template (without the .html)
	public static final String VIEW_ID = "default";
	public String getViewID() {
		return VIEW_ID;
	}
	
   private DevolvedSakaiSecurity devolvedSakaiSecurity;

	public void setDevolvedSakaiSecurity(DevolvedSakaiSecurity devolvedSakaiSecurity) {
		this.devolvedSakaiSecurity = devolvedSakaiSecurity;
	}
	
	private ToolManager toolManager;

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}
	
	private SiteService siteService;

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void fillComponents(UIContainer tofill, ViewParameters viewparams,
			ComponentChecker checker) {
		
		String siteId = toolManager.getCurrentPlacement().getContext();
		String reference = siteService.siteReference(siteId);
		List<Entity> sites = devolvedSakaiSecurity.findUsesOfAdmin(reference);
		
		for (Entity entity: sites) {
			UIBranchContainer siteRow = UIBranchContainer.make(tofill, "siteList:", entity.getId());
			if (entity instanceof Site) {
				Site site = (Site)entity;
				ResourceProperties siteProperties = site.getProperties();
				UILink.make(siteRow, "link", site.getTitle(), site.getUrl());
				if (siteProperties.getProperty("contact-email") != null) {
					UILink.make(siteRow, "contactLink", siteProperties.getProperty("contact-name"), "mailto:"+ siteProperties.getProperty("contact-email"));
				} else {
					UIOutput.make(siteRow, "contactName", siteProperties.getProperty("contact-name"));
				}
				if (site.getShortDescription() != null) {
					UIOutput.make(siteRow, "shortDescription", site.getShortDescription());
				}
			}
		}
		
	}

}
