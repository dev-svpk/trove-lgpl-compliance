package gnu.trove.map;

public interface TIntLongMap {
   long getNoEntryValue();

   long get(int var1);

   int size();

   gnu.trove.iterator.TIntLongIterator iterator();
}
