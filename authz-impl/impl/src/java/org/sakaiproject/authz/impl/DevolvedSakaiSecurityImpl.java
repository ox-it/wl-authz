package org.sakaiproject.authz.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.sakaiproject.authz.api.DevolvedSakaiSecurity;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.FunctionManager;
import org.sakaiproject.authz.impl.hbm.DevolvedAdmin;
import org.sakaiproject.authz.impl.hbm.DevolvedAdminDao;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.memory.api.Cache;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.user.api.User;

/**
 * Oxford version of Sakai security.
 * 
 * @author buckett
 * 
 */
public abstract class DevolvedSakaiSecurityImpl extends SakaiSecurity implements
		DevolvedSakaiSecurity {
	
	final public static String ADMIN_REALM_CHANGE = "site.admin.change";
	
	private static Log log = LogFactoryImpl
			.getLog(DevolvedSakaiSecurityImpl.class);
	
	private String adminSiteType;
	
	private Cache adminCache;
	
	protected abstract EventTrackingService eventTrackingService();
		
	public void init() {
		super.init();
		FunctionManager.registerFunction(ADMIN_REALM_PERMISSION);
		FunctionManager.registerFunction(ADMIN_REALM_PERMISSION_USE);
		log.info("Admin site type set to: "+ adminSiteType);
		
		adminCache = memoryService().newCache(DevolvedSakaiSecurityImpl.class.getName(), SiteService.REFERENCE_ROOT);
	}

	/**
	 * At the moment all implementations to unlock() call this.
	 */
	public boolean unlock(String userId, String function, String entityRef, Collection authz) {
		if (userId == null || function == null || entityRef == null) {
			log.warn("unlock(): null: " + userId + " " + function + " "+ entityRef);
			return false;
		}
		// if super, grant
		if (isSuperUser(userId))
		{
			return true;
		}
		// let the advisors have a crack at it, if we have any
		// Note: this cannot be cached without taking into consideration the
		// exact advisor configuration -ggolden
		if (hasAdvisors())
		{
			SecurityAdvisor.SecurityAdvice advice = adviseIsAllowed(userId, function, entityRef);
			if (advice != SecurityAdvisor.SecurityAdvice.PASS)
			{
				return advice == SecurityAdvisor.SecurityAdvice.ALLOWED;
			}
		}

		String adminRealm = getAdminRealm(entityRef);
		if (adminRealm != null) {
			if (log.isDebugEnabled()) {
				log.debug("Checking for admin in realm: " + adminRealm);
			}
			if (authz == null) {
				authz = entityManager().newReference(adminRealm).getAuthzGroups(userId);
			}
			authz = new ArrayList<String>(authz);
			// Add the admin authzgroups
			authz.addAll(entityManager().newReference(adminRealm).getAuthzGroups(userId));
			// Add the original authzgroups
			authz.addAll(entityManager().newReference(entityRef).getAuthzGroups(userId));
		}
		return checkAuthzGroups(userId, function, entityRef, authz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.authz.impl.DevolvedSakaiSecurity#getAdminRealm(java.lang.String)
	 */
	public String getAdminRealm(String entityRef) {
		String adminRealm = (String) adminCache.get(entityRef);
		// We want to cache nulls to need to look and see of the cache has an entry first.
		// If we check to see if the entry is in the cache first then our hit/miss stats our wrong.
		if (adminRealm == null && !adminCache.containsKey(entityRef) ) {
			DevolvedAdmin admin = dao().findByRealm(entityRef);
			adminRealm = (admin != null) ? admin.getAdminRealm() : null;
			adminCache.put(entityRef, adminRealm);
		}
		return adminRealm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.authz.impl.DevolvedSakaiSecurity#setAdminRealm(java.lang.String,
	 *      java.lang.String)
	 */
	public void setAdminRealm(String entityRef, String adminRealm)
			throws PermissionException {
		getSiteReference(entityRef);
		User user = userDirectoryService().getCurrentUser();
		if (!canSetAdminRealm(entityRef)) {
			throw new PermissionException(user.getId(), SiteService.SECURE_UPDATE_SITE, entityRef);
		}
		if (!canUseAdminRealm(adminRealm)) {
			throw new PermissionException(user.getId(), ADMIN_REALM_PERMISSION_USE, adminRealm);
		}
		DevolvedAdmin admin = dao().findByRealm(entityRef);
		if (admin == null) {
			admin = new DevolvedAdmin();
			admin.setRealm(entityRef);
		}
		admin.setAdminRealm(adminRealm);
		dao().save(admin);
		eventTrackingService().post(eventTrackingService().newEvent(ADMIN_REALM_CHANGE, entityRef, true));
	}

	public boolean canSetAdminRealm(String entityRef) {
		Reference ref = entityManager().newReference(entityRef);
		if (SiteService.APPLICATION_ID.equals(ref.getType())
				&& SiteService.SITE_SUBTYPE.equals(ref.getSubType())) {
			return unlock(SiteService.SECURE_UPDATE_SITE, entityRef);
		}
		return false;
	}
	
	public boolean canUseAdminRealm(String adminRealm) {
		return unlock(ADMIN_REALM_PERMISSION_USE, adminRealm);
	}
	
	public boolean canRemoveAdminRealm(String adminRealm) {
		return isSuperUser();
	}

	public List<Entity> getAvailableAdminRealms(String entityRef) {
		if (entityRef != null) {
			Reference ref = getSiteReference(entityRef);
		}
		// Must have some sort of filtering as we can't iterate over all the sites.
		List<Site> sites = siteService().getSites(SelectionType.ANY, adminSiteType, null, null, null, null);
		List <Entity> entities = new ArrayList<Entity>();
		for (Site site : sites) {
			if (unlock(ADMIN_REALM_PERMISSION_USE, site.getReference()) && !site.getReference().equals(entityRef)) {
				entities.add(site);
			}
		}
		return entities;
	}
	
	public List<Entity> findUsesOfAdmin(String adminRealm) {
		List<DevolvedAdmin> devolvedAdmins = dao().findByAdminRealm(adminRealm);
		List <Entity> sites = new ArrayList<Entity>(devolvedAdmins.size());
		for (DevolvedAdmin devolvedAdmin: devolvedAdmins)
		{
			Entity entity = getSiteReference(devolvedAdmin.getRealm()).getEntity();
			if (entity != null) {
				sites.add(entity);
			}
		}
		return sites;
	}
	
	public void removeAdminRealm(String adminRealm) throws PermissionException {
		Reference ref = getSiteReference(adminRealm);
		if (canRemoveAdminRealm(adminRealm)) {
			dao().delete(adminRealm);
		} else {
			throw new PermissionException(null,null,null);
		}
	}
	
	/**
	 * Get a reference for a site entity.
	 * @throws IllegalArgumentException If the entity supplied isn't a site one.
	 * @param entityRef 
	 * @return The reference.
	 */
	private Reference getSiteReference(String entityRef) {
		Reference ref = entityManager().newReference(entityRef);
		if (SiteService.APPLICATION_ID.equals(ref.getType())
				&& SiteService.SITE_SUBTYPE.equals(ref.getSubType())) {
			return ref;
		} else {
			throw new IllegalArgumentException(
					"Only site entities are supported at the moment. Entity: "
							+ entityRef);
		}
		
	}
	

	protected abstract SiteService siteService();

	protected abstract DevolvedAdminDao dao();

	protected abstract MemoryService memory();

	public String getAdminSiteType() {
		return adminSiteType;
	}

	public void setAdminSiteType(String adminSiteType) {
		this.adminSiteType = adminSiteType;
	}
}