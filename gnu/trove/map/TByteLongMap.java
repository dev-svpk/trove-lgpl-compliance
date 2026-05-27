package gnu.trove.map;

public interface TByteLongMap {
   long getNoEntryValue();

   long get(byte var1);

   int size();

   gnu.trove.iterator.TByteLongIterator iterator();
}
