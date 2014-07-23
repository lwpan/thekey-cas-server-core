package me.thekey.cas.admin.services.persondir;

import static me.thekey.cas.relay.Constants.PRINCIPAL_ATTR_RELAYGUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractFlatteningPersonAttributeDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TheKeyPersonAttributeDao extends AbstractFlatteningPersonAttributeDao {
    /* Set of the supported attributes */
    private static final Set<String> ATTRS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList
            (PRINCIPAL_ATTR_GUID, PRINCIPAL_ATTR_ADDITIONALGUIDS, PRINCIPAL_ATTR_EMAIL, PRINCIPAL_ATTR_FACEBOOKID,
                    PRINCIPAL_ATTR_RELAYGUID, PRINCIPAL_ATTR_FIRSTNAME, PRINCIPAL_ATTR_LASTNAME)));

    public IPersonAttributes getPerson(final String uid) {
        return null;
    }

    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(final Map<String, List<Object>> query) {
        return null;
    }

    public Set<String> getPossibleUserAttributeNames() {
        return ATTRS;
    }

    public Set<String> getAvailableQueryAttributes() {
        return null;
    }
}
