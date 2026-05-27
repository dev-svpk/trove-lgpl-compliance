package gnu.trove.map;

public interface TDoubleFloatMap {
   float getNoEntryValue();

   float get(double var1);

   int size();

   gnu.trove.iterator.TDoubleFloatIterator iterator();
}
