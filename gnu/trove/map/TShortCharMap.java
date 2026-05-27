package gnu.trove.map;

import gnu.trove.iterator.TShortCharIterator;

public interface TShortCharMap {
   char getNoEntryValue();

   char get(short var1);

   int size();

   TShortCharIterator iterator();
}
