package gnu.trove.map;

public interface TCharByteMap {
   byte getNoEntryValue();

   byte get(char var1);

   int size();

   gnu.trove.iterator.TCharByteIterator iterator();
}
