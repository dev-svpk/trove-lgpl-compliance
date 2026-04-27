package gnu.trove.map;

public interface TIntDoubleMap {
   double getNoEntryValue();

   double put(int var1, double var2);

   double get(int var1);

   void clear();

   int size();

   boolean containsKey(int var1);

   gnu.trove.iterator.TIntDoubleIterator iterator();
}
