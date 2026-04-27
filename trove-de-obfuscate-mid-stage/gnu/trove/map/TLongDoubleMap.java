package gnu.trove.map;

public interface TLongDoubleMap {
   double getNoEntryValue();

   double put(long var1, double var3);

   double get(long var1);

   int size();

   boolean containsKey(long var1);

   gnu.trove.iterator.TLongDoubleIterator iterator();
}
