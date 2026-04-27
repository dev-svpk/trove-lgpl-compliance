package gnu.trove.map;

public interface TIntFloatMap {
   float getNoEntryValue();

   float get(int var1);

   int size();

   gnu.trove.iterator.TIntFloatIterator iterator();
}
