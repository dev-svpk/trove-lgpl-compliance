package gnu.trove.map;

public interface TByteIntMap {
   int getNoEntryValue();

   int get(byte var1);

   int size();

   gnu.trove.iterator.TByteIntIterator iterator();
}
