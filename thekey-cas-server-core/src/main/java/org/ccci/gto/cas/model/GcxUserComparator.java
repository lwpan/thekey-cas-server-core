package org.ccci.gto.cas.model;

import java.util.Comparator;

import org.ccci.gcx.idm.core.model.impl.GcxUser;

public class GcxUserComparator implements Comparator<GcxUser> {
    public final static class Key {
	private enum Field {
	    FIRSTNAME, LASTNAME, EMAIL, USERID
	}

	final static public Key FIRSTNAME = new Key(Field.FIRSTNAME, true);
	final static public Key LASTNAME = new Key(Field.LASTNAME, true);
	final static public Key EMAIL = new Key(Field.EMAIL, true);
	final static public Key USERID = new Key(Field.USERID, true);

	final private Field field;
	final private boolean ignoreCase;

	private Key(final Field key, final boolean ignoreCase) {
	    this.field = key;
	    this.ignoreCase = ignoreCase;
	}

	public Key(final Key key, final boolean ignoreCase) {
	    this.field = key.field;
	    this.ignoreCase = ignoreCase;
	}
    }

    final private Key[] keys;

    public GcxUserComparator() {
	this(new Key[] { Key.LASTNAME, Key.FIRSTNAME, Key.USERID, Key.EMAIL });
    }

    public GcxUserComparator(final Key[] keys) {
	this.keys = keys;
    }

    public int compare(final GcxUser user1, final GcxUser user2) {
	for (final Key key : keys) {
	    final String val1;
	    final String val2;
	    switch (key.field) {
	    case FIRSTNAME:
		val1 = user1.getFirstName();
		val2 = user2.getFirstName();
		break;
	    case LASTNAME:
		val1 = user1.getLastName();
		val2 = user2.getLastName();
		break;
	    case EMAIL:
		val1 = user1.getEmail();
		val2 = user2.getEmail();
		break;
	    case USERID:
	    default:
		val1 = user1.getUserid();
		val2 = user2.getUserid();
		break;
	    }

	    final int response = key.ignoreCase ? val1
		    .compareToIgnoreCase(val2) : val1.compareTo(val2);
	    if (response != 0) {
		return response;
	    }
	}

	return 0;
    }
}
