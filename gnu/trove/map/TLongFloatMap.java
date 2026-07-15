package gnu.trove.map;

public interface TLongFloatMap {
   float getNoEntryValue();

   float get(long var1);

   int size();

   gnu.trove.iterator.TLongFloatIterator iterator();
}
