package gnu.trove.map;

import gnu.trove.iterator.TShortIntIterator;

public interface TShortIntMap {
   int getNoEntryValue();

   int get(short var1);

   int size();

   TShortIntIterator iterator();
}
