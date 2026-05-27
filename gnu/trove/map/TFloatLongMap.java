package gnu.trove.map;

public interface TFloatLongMap {
   long getNoEntryValue();

   long get(float var1);

   int size();

   gnu.trove.iterator.TFloatLongIterator iterator();
}
