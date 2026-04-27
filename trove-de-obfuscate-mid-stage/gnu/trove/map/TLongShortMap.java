package gnu.trove.map;

public interface TLongShortMap {
   short getNoEntryValue();

   short get(long var1);

   int size();

   gnu.trove.iterator.TLongShortIterator iterator();
}
