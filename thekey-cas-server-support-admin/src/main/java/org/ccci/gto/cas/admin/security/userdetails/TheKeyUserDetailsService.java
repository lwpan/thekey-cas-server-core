package org.ccci.gto.cas.admin.security.userdetails;

import java.util.HashSet;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TheKeyUserDetailsService implements UserDetailsService {
    /** Instance of logging for subclasses. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @NotNull
    private GcxUserService userService;

    @NotNull
    private String adminGroupDn;

    /**
     * @param adminGroupDn
     *            the adminGroupDn to set
     */
    public void setAdminGroupDn(final String adminGroupDn) {
	this.adminGroupDn = adminGroupDn;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final GcxUserService userService) {
	this.userService = userService;
    }

    /**
     * @return the userService
     */
    protected GcxUserService getUserService() {
	return this.userService;
    }

    public UserDetails loadUserByUsername(final String username)
	    throws UsernameNotFoundException, DataAccessException {
	// look up the specified user
	final GcxUser keyUser = this.getUserService().findUserByEmail(username);
	if (keyUser == null) {
	    throw new UsernameNotFoundException("User not found: " + username);
	}

	// setup the user's authority
        final HashSet<GrantedAuthority> authority = new HashSet<GrantedAuthority>();
	if (keyUser.getGroupMembership().contains(this.adminGroupDn)) {
	    authority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	// create and return a user object
	return new User(keyUser.getEmail(), "", true, true, true, true,
		authority);
    }
}
