package javax.microedition.pim;

public abstract interface ContactList
		extends PIMList {
	public abstract Contact createContact();

	public abstract Contact importContact(Contact paramContact);

	public abstract void removeContact(Contact paramContact)
			throws PIMException;
}
