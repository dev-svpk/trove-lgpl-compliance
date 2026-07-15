package gnu.trove.map;

public interface TDoubleCharMap {
   char getNoEntryValue();

   char get(double var1);

   int size();

   gnu.trove.iterator.TDoubleCharIterator iterator();
}
