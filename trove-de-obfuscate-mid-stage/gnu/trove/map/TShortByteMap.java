package gnu.trove.map;

import gnu.trove.iterator.TShortByteIterator;

public interface TShortByteMap {
   byte getNoEntryValue();

   byte get(short var1);

   int size();

   TShortByteIterator iterator();
}
