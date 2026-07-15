package gnu.trove.map;

public interface TByteShortMap {
   short getNoEntryValue();

   short get(byte var1);

   int size();

   gnu.trove.iterator.TByteShortIterator iterator();
}
