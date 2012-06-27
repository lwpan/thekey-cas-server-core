package org.ccci.gto.cas.services.persondir;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractFlatteningPersonAttributeDao;

public class TheKeyPersonAttributeDao extends
	AbstractFlatteningPersonAttributeDao {
    /** Set of the supported attributes */
    private static final Set<String> attrs;
    static {
	final HashSet<String> rawSet = new HashSet<String>();
	rawSet.add(PRINCIPAL_ATTR_GUID);
	rawSet.add(PRINCIPAL_ATTR_ADDITIONALGUIDS);
	rawSet.add(PRINCIPAL_ATTR_EMAIL);
        rawSet.add(PRINCIPAL_ATTR_FACEBOOKID);
	rawSet.add(PRINCIPAL_ATTR_FIRSTNAME);
	rawSet.add(PRINCIPAL_ATTR_LASTNAME);
	attrs = Collections.unmodifiableSet(rawSet);
    }

    public IPersonAttributes getPerson(String uid) {
	return null;
    }

    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(
	    Map<String, List<Object>> query) {
	return null;
    }

    public Set<String> getPossibleUserAttributeNames() {
	return attrs;
    }

    public Set<String> getAvailableQueryAttributes() {
	return null;
    }
}
