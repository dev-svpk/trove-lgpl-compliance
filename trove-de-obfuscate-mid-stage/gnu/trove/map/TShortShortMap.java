package gnu.trove.map;

import gnu.trove.iterator.TShortShortIterator;

public interface TShortShortMap {
   short getNoEntryValue();

   short get(short var1);

   int size();

   TShortShortIterator iterator();
}
