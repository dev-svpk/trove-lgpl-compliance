package gnu.trove.map;

public interface TCharFloatMap {
   float getNoEntryValue();

   float get(char var1);

   int size();

   gnu.trove.iterator.TCharFloatIterator iterator();
}
