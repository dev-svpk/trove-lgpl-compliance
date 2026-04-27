package gnu.trove.map;

public interface TCharLongMap {
   long getNoEntryValue();

   long get(char var1);

   int size();

   gnu.trove.iterator.TCharLongIterator iterator();
}
