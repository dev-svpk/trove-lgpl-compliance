package gnu.trove.map;

public interface TLongIntMap {
   int getNoEntryValue();

   int put(long var1, int var3);

   int get(long var1);

   int size();

   gnu.trove.iterator.TLongIntIterator iterator();
}
