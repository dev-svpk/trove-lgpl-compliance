package gnu.trove.map;

public interface TCharDoubleMap {
   double getNoEntryValue();

   double get(char var1);

   int size();

   gnu.trove.iterator.TCharDoubleIterator iterator();
}
