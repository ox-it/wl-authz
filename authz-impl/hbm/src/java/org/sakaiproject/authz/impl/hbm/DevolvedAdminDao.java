package org.sakaiproject.authz.impl.hbm;

import java.util.List;


public interface DevolvedAdminDao {

	List<DevolvedAdmin> findByAdminRealm(String adminRealm);
	
	DevolvedAdmin findByRealm(String realm);
	
	void save(DevolvedAdmin devolvedAdmin);
	
	void delete(String realm);
}
