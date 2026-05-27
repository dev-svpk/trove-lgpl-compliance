package gnu.trove.map;

import gnu.trove.iterator.TShortFloatIterator;

public interface TShortFloatMap {
   float getNoEntryValue();

   float get(short var1);

   int size();

   TShortFloatIterator iterator();
}
