package gnu.trove.map;

public interface TCharIntMap {
   int getNoEntryValue();

   int get(char var1);

   int size();

   gnu.trove.iterator.TCharIntIterator iterator();
}
