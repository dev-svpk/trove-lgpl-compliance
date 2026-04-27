package gnu.trove.map;

public interface TDoubleIntMap {
   int getNoEntryValue();

   int get(double var1);

   int size();

   gnu.trove.iterator.TDoubleIntIterator iterator();
}
