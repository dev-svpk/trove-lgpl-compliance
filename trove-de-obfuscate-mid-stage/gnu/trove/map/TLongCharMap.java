package gnu.trove.map;

public interface TLongCharMap {
   char getNoEntryValue();

   char get(long var1);

   int size();

   gnu.trove.iterator.TLongCharIterator iterator();
}
