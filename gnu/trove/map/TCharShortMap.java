package gnu.trove.map;

public interface TCharShortMap {
   short getNoEntryValue();

   short get(char var1);

   int size();

   gnu.trove.iterator.TCharShortIterator iterator();
}
