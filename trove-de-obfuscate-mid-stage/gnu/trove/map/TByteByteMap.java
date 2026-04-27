package gnu.trove.map;

public interface TByteByteMap {
   byte getNoEntryValue();

   byte get(byte var1);

   int size();

   gnu.trove.iterator.TByteByteIterator iterator();
}
