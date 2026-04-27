package gnu.trove.map;

public interface TFloatByteMap {
   byte getNoEntryValue();

   byte get(float var1);

   int size();

   gnu.trove.iterator.TFloatByteIterator iterator();
}
