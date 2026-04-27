package gnu.trove.map;

public interface TIntByteMap {
   byte getNoEntryValue();

   byte get(int var1);

   int size();

   gnu.trove.iterator.TIntByteIterator iterator();
}
