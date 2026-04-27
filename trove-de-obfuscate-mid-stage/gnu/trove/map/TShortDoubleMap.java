package gnu.trove.map;

import gnu.trove.iterator.TShortDoubleIterator;

public interface TShortDoubleMap {
   double getNoEntryValue();

   double get(short var1);

   int size();

   TShortDoubleIterator iterator();
}
