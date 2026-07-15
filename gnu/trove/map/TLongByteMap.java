package gnu.trove.map;

public interface TLongByteMap {
   byte getNoEntryValue();

   byte get(long var1);

   int size();

   gnu.trove.iterator.TLongByteIterator iterator();
}
