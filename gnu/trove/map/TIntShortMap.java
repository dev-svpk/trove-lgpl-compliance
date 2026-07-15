package gnu.trove.map;

public interface TIntShortMap {
   short getNoEntryValue();

   short get(int var1);

   int size();

   gnu.trove.iterator.TIntShortIterator iterator();
}
