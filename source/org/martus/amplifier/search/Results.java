package org.martus.amplifier.search;

public interface Results
{
	int getCount() throws BulletinIndexException;
	BulletinInfo getBulletinInfo(int n) throws BulletinIndexException;
}
