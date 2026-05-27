package gnu.trove.map;

public interface TDoubleDoubleMap {
   double getNoEntryValue();

   double get(double var1);

   int size();

   gnu.trove.iterator.TDoubleDoubleIterator iterator();
}
