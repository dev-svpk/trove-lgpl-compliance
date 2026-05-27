package gnu.trove.map;

public interface TFloatCharMap {
   char getNoEntryValue();

   char get(float var1);

   int size();

   gnu.trove.iterator.TFloatCharIterator iterator();
}
