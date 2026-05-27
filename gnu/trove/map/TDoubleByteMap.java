package gnu.trove.map;

public interface TDoubleByteMap {
   byte getNoEntryValue();

   byte get(double var1);

   int size();

   gnu.trove.iterator.TDoubleByteIterator iterator();
}
