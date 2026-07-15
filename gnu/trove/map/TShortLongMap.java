package gnu.trove.map;

import gnu.trove.iterator.TShortLongIterator;

public interface TShortLongMap {
   long getNoEntryValue();

   long get(short var1);

   int size();

   TShortLongIterator iterator();
}
