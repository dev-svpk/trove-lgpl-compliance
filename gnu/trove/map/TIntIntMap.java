package gnu.trove.map;

public interface TIntIntMap {
   int getNoEntryValue();

   int put(int var1, int var2);

   int get(int var1);

   int size();

   gnu.trove.iterator.TIntIntIterator iterator();
}
