package gnu.trove.map;

public interface TDoubleLongMap {
   long getNoEntryValue();

   long get(double var1);

   int size();

   gnu.trove.iterator.TDoubleLongIterator iterator();
}
